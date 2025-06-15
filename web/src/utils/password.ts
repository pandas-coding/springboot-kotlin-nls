import bcrypt from 'bcryptjs'

const saltRounds = 10;

/**
 * generate hashed password
 * @param password
 */
export function hashPassword(password: string): string {
  const salt = bcrypt.genSaltSync(saltRounds)
  return bcrypt.hashSync(password, salt)
}