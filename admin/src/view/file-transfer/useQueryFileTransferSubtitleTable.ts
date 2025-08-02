import { reactive, ref, shallowRef, toValue } from 'vue'
import type { ColumnsType } from 'ant-design-vue/es/table'
import restService from '@/service'
import qs from 'qs'
import { formatSecond } from '@/utils/format-time.ts'
import type { TablePaginationConfig } from 'ant-design-vue'
import type { MaybeRefOrGetter } from '@vueuse/core'

export const useQueryFileTransferSubtitleTable = (params: {
  fileTransferId: MaybeRefOrGetter<string>,
}) => {
  const subtitleList = shallowRef([])
  const columns = shallowRef(DEFAULT_SUBTITLE_COLUMNS)
  const pagination = reactive({
    total: 0,
    current: 1,
    pageSize: 10,
  })
  const loading = ref(false)

  const querySubtitleList = async (fileTransferId: string, queryPagination: {
    page?: number,
    size?: number,
  } = { page: 1, size: pagination.pageSize }) => {
    loading.value = true
    const respData = await restService.get('/nls/admin/file-transfer-subtitle/query', {
      paramsSerializer: params => qs.stringify(params, { allowDots: true }),
      params: {
        fileTransferId: fileTransferId,
        pagination: {
          page: queryPagination.page,
          size: queryPagination.size,
        },
      },
    })

    loading.value = false
    if (!respData.success) {
      subtitleList.value = []
      return
    }

    const queryResult = respData.content!!
    subtitleList.value = queryResult.list
    pagination.total = queryResult.total
    pagination.current = queryResult.pageNum
  }

  const handleTableChange = (pagination: TablePaginationConfig) => {
    querySubtitleList(toValue(params.fileTransferId), {
      page: pagination.current,
      size: pagination.pageSize,
    })
  }
  
  return {
    subtitleList,
    columns,
    pagination,
    loading,
    querySubtitleList,
    handleTableChange,
  }
}


export interface FileTransferSubtitleRecord {
  id: number;
  fileTransferId: number;
  index: number;
  begin: number;
  end: number;
  text: string;
  createdAt?: string | null;
  updatedAt?: string | null;
}

const DEFAULT_SUBTITLE_COLUMNS: ColumnsType<FileTransferSubtitleRecord> = [
  // {
  //   title: '开始时间',
  //   dataIndex: 'begin',
  // },
  // {
  //   title: '结束时间',
  //   dataIndex: 'end',
  // },
  {
    title: '时间',
    dataIndex: 'time',
    customRender: ({record}) => {
      const beginSecond = formatSecond(record.begin / 1000)
      const endSecond = formatSecond(record.end / 1000)
      return `${beginSecond} ~ ${endSecond}`
    }
  },
  {
    title: '字幕',
    dataIndex: 'text',
  },
]