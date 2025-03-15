## Auth

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "username": "newUsers",
           "password": "securePassword"
         }'
```


### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
           "username": "newUsers",
           "email": "user@yanto.com",
           "password": "securePassword",
           "role": ["USER"]
         }'
```

### Get ME
```bash
curl -X GET http://localhost:8080/api/auth/me \
     -H "Authorization: Bearer <token>"
```

### Verify Email
```bash
curl -X GET http://localhost:8080/api/auth/verify?token=yourVerificationToken \
     -H "Authorization: Bearer <token>"
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
     -H "Content-Type: application/json" \
     -d '{
           "refreshToken": "yourRefreshToken"
         }'
```

### Forgot Password
```bash
curl -X POST http://localhost:8080/api/auth/forgot \
     -H "Content-Type: application/json" \
     -d '{
           "email": "user@example.com"
         }'
```

### Reset Password
```bash
curl -X POST http://localhost:8080/api/auth/reset \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -d '{
           "token": "resetTokenHere",
           "password": "newSecurePassword"
         }'
```


### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```


## Room

### FindAll Room

```bash
curl -X GET http://localhost:8080/api/room \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Find Id Room

```bash
curl -X GET "http://localhost:8080/api/room/{id}" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Create Room

```bash
curl -X POST http://localhost:8080/api/room/create \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@/path/to/image.jpg" \
     -F "roomName=Conference Room A" \
     -F "roomCapacity=10"
```


### Update Room

```bash
curl -X PUT "http://localhost:8080/api/room/update/{id}" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: multipart/form-data" \
     -F "file=@/path/to/new_image.jpg" \
     -F "roomName=Updated Conference Room" \
     -F "roomCapacity=15"
```
### Delete Room

```bash
curl -X GET "http://localhost:8080/api/room/delete/{id}" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```



## Booking

### FindAll Booking

```bash
curl -X GET http://localhost:8080/api/bookings \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Find Id Booking

```bash
curl -X GET "http://localhost:8080/api/bookings/{id}" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Create Booking

```bash
curl -X POST http://localhost:8080/api/bookings/create \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -d '{
           "roomId": 123,
           "totalPerson": 2,
           "bookingTime": "2025-03-20T14:00:00",
           "noted": "Please prepare extra pillows"
         }'

```

### Update Booking

```bash
curl -X POST "http://localhost:8080/api/bookings/update/{id}" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -d '{
           "roomId": 456,
           "totalPerson": 3,
           "bookingTime": "2025-04-01T12:00:00",
           "noted": "Change room to deluxe"
         }'
```


### Delete Booking

```bash
curl -X GET "http://localhost:8080/api/bookings/delete/{id}" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```
