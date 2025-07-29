/**
 * 随机生成[len]长度的[radix]进制数
 * @param len
 * @param radix
 */
export function uuid(len: number, radix: number = 62) {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

  const uuid = [];
  radix = radix || chars.length;

  for (let i = 0; i < len; i++) {
    uuid[i] = chars[0 | Math.random() * radix];
  }

  return uuid.join('');
}

/**
 * 时间格式化
 * @param value
 * @param showEmptyHour
 */
export function formatSecond(value: string = String(0), showEmptyHour: boolean = false) {
  let second = parseInt(value, 10); // 秒
  let minute = 0; // 分
  let hour = 0; // 小时
  if (second >= 60) {
    // 当大于60秒时，才需要做转换
    minute = Math.floor(second / 60);
    second = Math.floor(second % 60);
    if (minute >= 60) {
      hour = Math.floor(minute / 60);
      minute = Math.floor(minute % 60);
    }
  } else {
    // 小于60秒时，直接显示，不需要处理
  }
  let result = "" + prefixInteger(second, 2) + "";
  // 拼上分钟
  result = "" + prefixInteger(minute, 2) + ":" + result;
  if (showEmptyHour) {
    // 拼上小时
    result = "" + prefixInteger(hour, 2) + ":" + result;
  } else if ( hour > 0) {
    // 拼上小时
    result = "" + (hour < 10 ? prefixInteger(hour, 2) : hour) + ":" + result;
  }
  return result;
}

/**
 * 格式化指定长度，前面补0
 * @param num
 * @param length
 */
export function prefixInteger(num: number, length: number) {
  return (Array(length).join('0') + num).slice(-length);
}
