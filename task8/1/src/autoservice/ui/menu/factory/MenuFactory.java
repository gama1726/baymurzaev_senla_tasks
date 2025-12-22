package autoservice.ui.menu.factory;

import autoservice.ui.menu.MenuItem;

public interface MenuFactory {

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

    MenuItem createListMechanicsItem();
    MenuItem createAddMechanicItem();
    MenuItem createRemoveMechanicItem();

    MenuItem createListGarageSlotsItem();
    MenuItem createListFreeGarageSlotsNowItem();
    MenuItem createAddGarageSlotItem();
    MenuItem createRemoveGarageSlotItem();

    MenuItem createFreeCapacityAtItem();
    MenuItem createNextFreeDateItem();
}

