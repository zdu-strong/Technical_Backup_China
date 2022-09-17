import linq from "linq";

export function PaginationModel<T>(
  pageNum: number,
  pageSize: number,
  stream: linq.IEnumerable<T>
) {
  if (pageSize < 1) {
    throw new Error("The page number cannot be less than 1");
  }
  if (pageNum < 1) {
    throw new Error("The page size cannot be less than 1");
  }

  const totalRecord = stream.count();
  const totalPage = Math.ceil(totalRecord / pageSize);
  const list = stream
    .skip((pageNum - 1) * pageSize)
    .take(pageSize)
    .toArray();
  return {
    pageNum,
    pageSize,
    totalRecord,
    totalPage,
    list,
  };
}
