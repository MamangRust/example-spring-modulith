package com.sanedge.modularexample.components;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sanedge.modularexample.booking.models.Booking;
import com.sanedge.modularexample.booking.repository.BookingRepository;
import com.sanedge.modularexample.role.enums.ERole;
import com.sanedge.modularexample.role.models.Role;
import com.sanedge.modularexample.role.repository.RoleRepository;
import com.sanedge.modularexample.room.enums.RoomStatus;
import com.sanedge.modularexample.room.models.Room;
import com.sanedge.modularexample.room.repository.RoomRepository;
import com.sanedge.modularexample.user.models.User;
import com.sanedge.modularexample.user.repository.UserRepository;

@Component
public class DatabaseSender implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseSender(RoleRepository roleRepository, UserRepository userRepository,
            BookingRepository bookingRepository, RoomRepository roomRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        this.seedRoles();
        this.seedUsers();
        this.seedRooms();
        this.seedBookings();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role admin = new Role();

            admin.setName(ERole.ROLE_ADMIN);

            Role mod = new Role();

            mod.setName(ERole.ROLE_MODERATOR);

            Role user = new Role();
            user.setName(ERole.ROLE_USER);

            roleRepository.save(admin);
            roleRepository.save(mod);
            roleRepository.save(user);
        }

    }

    private void seedUsers() {

        if (userRepository.count() == 0) {
            Optional<Role> admin = this.roleRepository.findByName(ERole.ROLE_ADMIN);
            Optional<Role> user = this.roleRepository.findByName(ERole.ROLE_USER);

            User adminUser = new User();
            User userUser = new User();

            Set<Role> adminRoles = new HashSet<>();
            Set<Role> userRoles = new HashSet<>();

            adminRoles.add(admin.get());
            adminRoles.add(user.get());

            userRoles.add(user.get());

            adminUser.setUsername("Yantoo");
            adminUser.setEmail("yanto@email.com");
            adminUser.setPassword(passwordEncoder.encode("yantogaming"));
            adminUser.setRoles(adminRoles);

            userUser.setUsername("user");
            userUser.setEmail("user@gmail");
            userUser.setPassword(passwordEncoder.encode("user"));
            userUser.setRoles(userRoles);

            this.userRepository.save(userUser);
            this.userRepository.save(adminUser);

        }
    }

    private void seedRooms() {
        if (roomRepository.count() == 0) {
            Room room_1 = new Room();
            room_1.setRoomName("Conference Room 1");
            room_1.setRoomCapacity(10);
            room_1.setPhoto("conference_room_1.jpg");
            room_1.setRoomStatus(RoomStatus.READY);

            Room room_2 = new Room();
            room_2.setRoomName("Conference Room 2");
            room_2.setRoomCapacity(15);
            room_2.setPhoto("conference_room_2.jpg");
            room_2.setRoomStatus(RoomStatus.READY);

            roomRepository.save(room_1);
            roomRepository.save(room_2);
        }
    }

    private void seedBookings() {
        if (bookingRepository.count() == 0) {
            User user = userRepository.findByUsername("user").orElse(null);
            Room room = roomRepository.findByRoomName("Conference Room 1").orElse(null);

            if (user != null && room != null) {
                Booking booking = new Booking();

                booking.setOrderId("ORDER" + System.currentTimeMillis());
                booking.setUser(user);
                booking.setRoom(room);
                booking.setTotalPerson(5);
                booking.setBookingTime(LocalDateTime.now().plusDays(1));
                booking.setNoted("Meeting with clients");
                booking.setCheckInTime(LocalDateTime.now().plusDays(1));
                booking.setCheckOutTime(LocalDateTime.now().plusDays(2));

                bookingRepository.save(booking);
            }
        }
    }
}