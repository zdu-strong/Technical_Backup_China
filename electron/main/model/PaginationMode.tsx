import linq from "linq";
import * as mathjs from 'mathjs';

export function PaginationModel<T>(
  pageNum: number,
  pageSize: number,
  stream: linq.IEnumerable<T>
) {

  if (pageNum < 1) {
    throw new Error("The page number cannot be less than 1");
  }
  if (pageSize < 1) {
    throw new Error("The page size cannot be less than 1");
  }

  if (pageNum !== Math.floor(pageNum)) {
    throw new Error("The page number must be an integer");
  }

  if (pageSize !== Math.floor(pageSize)) {
    throw new Error("The page size must be an integer");
  }

  const totalRecord = stream.count();
  const totalPage = Math.ceil(mathjs.divide(Math.floor(totalRecord), Math.floor(pageSize)));
  const list = stream
    .skip(mathjs.multiply(pageNum - 1, pageSize))
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
