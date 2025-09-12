export interface LoginRequest {
  username: string;
  password: string;
}
 
export interface AuthResponse{
  accessToken: string;
  username: string ;
  email: string;
  roles: string [];

}
