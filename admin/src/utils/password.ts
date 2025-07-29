import bcrypt from 'bcryptjs'
import SparkMD5 from 'spark-md5'

const saltRounds = 10;

/**
 * generate hashed password
 * @param password
 */
export function hashPassword(password: string): string {
  const salt = bcrypt.genSaltSync(saltRounds)
  return bcrypt.hashSync(password, salt)
}

/**
 * base64 encoding a md5 hashed string
 * @param s
 */
export function base64MD5String(s: string): string {
  const rawMd5 = SparkMD5.hash(s, true)
  return btoa(rawMd5)
}
