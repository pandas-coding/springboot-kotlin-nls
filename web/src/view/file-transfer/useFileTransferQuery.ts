import { reactive, ref, shallowRef, toValue } from 'vue'
import restService from '@/service/rest-service.ts'
import qs from 'qs'

type FileTransferQueryResult = {
  list: [],
  total: number,
  pageNum: number,
  pageSize: number,
  pages: number,
}

export type FileTransferColumn = {
  title: string
  dataIndex: string
}

const DEFAULT_COLUMNS: FileTransferColumn[] = [{
  title: '名称',
  dataIndex: 'name',
}, {
  title: '支付状态',
  dataIndex: 'payStatus',
}, {
  title: '状态',
  dataIndex: 'status',
}, {
  title: '语言',
  dataIndex: 'lang',
}, {
  title: '时长',
  dataIndex: 'second',
}, {
  title: '操作',
  dataIndex: 'operation',
}]

export const useFileTransferQuery = () => {
  const fileTransferList = shallowRef([])
  const columns = shallowRef(DEFAULT_COLUMNS)
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
    columns,
    pagination,
    loading,
    queryFileTransferList,
  }
}
