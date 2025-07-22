import { reactive, ref, shallowRef, toValue } from 'vue'
import restService from '@/service/rest-service.ts'
import qs from 'qs'
import type { ColumnsType } from 'ant-design-vue/es/table'
import {
  FILE_TRANSFER_LANG_ARRAY,
  FILE_TRANSFER_PAY_STATUS_ARRAY,
  FILE_TRANSFER_STATUS,
  FILE_TRANSFER_STATUS_ARRAY,
} from '../../../public/js/enums.ts'
import { formatSecond } from '@/utils/format-time.ts'
import { Button } from 'ant-design-vue'

type FileTransferQueryResult = {
  list: FileTransferRecord[],
  total: number,
  pageNum: number,
  pageSize: number,
  pages: number,
}

export interface FileTransferRecord {
  id: string; // LongAsString
  memberId: number;
  name: string;
  second: number;
  amount: string; // BigDecimalToString
  audio: string;
  fileSign: string;
  payStatus: string;
  status: string;
  lang: string;
  vod: string;
  taskId?: string | null;
  transStatusCode?: number | null;
  transStatusText?: string | null;
  transTime?: string | null; // LocalDateTime
  solveTime?: string | null; // LocalDateTime
  createdAt: string; // LocalDateTime
  updatedAt?: string | null; // LocalDateTime
}

const payStatusMap = FILE_TRANSFER_PAY_STATUS_ARRAY
  .reduce(
    (acc, option) => acc.set(option.code, option),
    new Map<(typeof FILE_TRANSFER_PAY_STATUS_ARRAY)[number]['code'], (typeof FILE_TRANSFER_PAY_STATUS_ARRAY)[number]>(),
  )

const statusMap = FILE_TRANSFER_STATUS_ARRAY
  .reduce(
    (acc, option) => acc.set(option.code, option),
    new Map<(typeof FILE_TRANSFER_STATUS_ARRAY)[number]['code'], (typeof FILE_TRANSFER_STATUS_ARRAY)[number]>(),
  )

const langMap = FILE_TRANSFER_LANG_ARRAY
  .reduce(
    (acc, option) => acc.set(option.code, option),
    new Map<(typeof FILE_TRANSFER_LANG_ARRAY)[number]['code'], (typeof FILE_TRANSFER_LANG_ARRAY)[number]>(),
  )

/**
 * 分页查询语音识别文件列表
 */
export const useFileTransferQuery = () => {
  const fileTransferList = shallowRef<FileTransferRecord[]>([])
  const pagination = reactive({
    total: 0,
    current: 1,
    pageSize: 10,
  })
  const loading = ref(false)

  const queryFileTransferList = async (
    param: { page: number, size: number } = { page: 1, size: pagination.pageSize },
  ) => {
    const _queryParam = toValue(param)

    loading.value = true
    const respData = await restService.get<FileTransferQueryResult>('/nls/web/file-transfer/query', {
      params: {
        pagination: {
          page: _queryParam.page,
          size: _queryParam.size,
        },
      },
      paramsSerializer: params => {
        return qs.stringify(params, { allowDots: true })
      },
    })

    loading.value = false
    if (!respData.success) {
      fileTransferList.value = []
      return
    }

    const queryResult = respData.content!!
    fileTransferList.value = queryResult.list
    pagination.total = queryResult.total
    pagination.current = queryResult.pageNum
  }


  return {
    fileTransferList,
    pagination,
    loading,
    queryFileTransferList,
  }
}

type UseColumnsParams = {
  onClickColumnOperation: (record: FileTransferRecord) => void
}

export const useColumns = (params: UseColumnsParams) => {
  const DEFAULT_COLUMNS = shallowRef<ColumnsType<FileTransferRecord>>([
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '支付状态',
      dataIndex: 'payStatus',
      customRender: ({record}) => payStatusMap.get(record.payStatus)?.desc,
    },
    {
      title: '状态',
      dataIndex: 'status',
      customRender: ({record}) => statusMap.get(record.status)?.desc,
    },
    {
      title: '语言',
      dataIndex: 'lang',
      customRender: ({record}) => langMap.get(record.lang)?.desc,
    },
    {
      title: '时长',
      dataIndex: 'second',
      customRender: ({record}) => formatSecond(record.second, true),
    },
    {
      title: '操作',
      dataIndex: 'operation',
      customRender: ({record}) => record.status === FILE_TRANSFER_STATUS.SUBTITLE_SUCCESS.code
        ? (<Button type='primary' onClick={() => params.onClickColumnOperation(record)}></Button>)
        : null
    },
  ])

  return {
    columns: DEFAULT_COLUMNS,
  }
}
