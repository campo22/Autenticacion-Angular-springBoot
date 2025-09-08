export interface UserDto {
  username: string;
  email: string;
  password: string;
  roles: Set<string>;

}
