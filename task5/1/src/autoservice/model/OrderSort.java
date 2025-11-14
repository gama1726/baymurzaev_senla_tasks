package autoservice.model;
/**
 * Варианты сортировки заказов.
 */
public enum OrderSort {
    BY_SUBMIT_DATE, //дата подачи
    BY_PLANNED_START, //дата планируемого начала
    BY_FINISH_DATE, //дата завершения
    BY_PRICE;       //цена
}
