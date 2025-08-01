import SparkMD5 from 'spark-md5'

/**
 * generate hashed password
 * @param password
 */
export function hashPassword(password: string): string {
  return SparkMD5.hash(password)
}

/**
 * base64 encoding a md5 hashed string
 * @param s
 */
export function base64MD5String(s: string): string {
  const rawMd5 = SparkMD5.hash(s)
  return btoa(rawMd5)
}
