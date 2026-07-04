package util;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 * 对内存中的列表进行分页截取
 */
public class PaginationUtil {

    /**
     * 对列表进行分页
     * @param list 原始列表
     * @param page 页码（从1开始）
     * @param pageSize 每页条数
     * @return 当前页数据
     */
    public static <T> List<T> paginate(List<T> list, int page, int pageSize) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        int total = list.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        return new ArrayList<>(list.subList(start, end));
    }
}
