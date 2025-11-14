package autoservice.ui.menu.factory;

import autoservice.ui.menu.MenuItem;

/**
 * Фабрика для создания пунктов меню, связанных с заказами.
 */
public interface OrderMenuFactory {
    MenuItem createListOrdersItem();
    MenuItem createListCurrentOrdersItem();
    MenuItem createCreateOrderItem();
    MenuItem createCloseOrderItem();
    MenuItem createCancelOrderItem();
    MenuItem createDeleteOrderItem();
    MenuItem createShiftOrderItem();
    MenuItem createOrderByMechanicNowItem();
    MenuItem createMechanicByOrderItem();
    MenuItem createOrdersByPeriodItem();
}

