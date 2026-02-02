# TODO'S:

## Application Contoller / Service
- Test application serivce & edge cases
- Implement delete endpoint
- Remove full exposure of Company and Interview entities in ApplicaitonResponseDto
- Change get by title for get by id in controller
- Implement filters and pagination

## Other
- Implement security configs (SecurityFilterChain) - **priority**
- Implement auth (Stateless - JWT, Method-level auth) - **priority**
- Create additional controller & service layers (user...)
- Implement optimistic locking (Users could update the same table values at once)
- Create a job board (Multiple users can share a single "job board", track together)
- Enable CORS for frontend integration