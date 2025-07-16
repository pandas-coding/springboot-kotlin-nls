/**
 * 将总秒数格式化为 HH:mm:ss 或 mm:ss 格式的字符串。
 * @param totalSeconds - 总秒数
 * @param showEmptyHour - 是否在小时为0时也显示小时部分 (例如 "00:15:30")
 * @returns 格式化后的时间字符串
 */
export const formatSecond = (totalSeconds: number = 0, showEmptyHour: boolean = false): string => {
  // 确保输入是有效的非负整数
  const secondsValue = Math.floor(Math.max(0, totalSeconds));

  // 1. 直接计算出小时、分钟和秒
  const hours = Math.floor(secondsValue / 3600);
  const minutes = Math.floor((secondsValue % 3600) / 60);
  const seconds = secondsValue % 60;

  // 2. 使用 padStart 补零，确保是两位数
  const formattedMinutes = String(minutes).padStart(2, '0');
  const formattedSeconds = String(seconds).padStart(2, '0');

  // 3. 根据条件拼接字符串
  if (hours > 0 || showEmptyHour) {
    const formattedHours = String(hours).padStart(2, '0');
    return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
  } else {
    return `${formattedMinutes}:${formattedSeconds}`;
  }
}