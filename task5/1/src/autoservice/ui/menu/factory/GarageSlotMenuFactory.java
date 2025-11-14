package autoservice.ui.menu.factory;

import autoservice.ui.menu.MenuItem;

/**
 * Фабрика для создания пунктов меню, связанных с гаражными местами.
 */
public interface GarageSlotMenuFactory {
    MenuItem createListGarageSlotsItem();
    MenuItem createListFreeGarageSlotsNowItem();
    MenuItem createAddGarageSlotItem();
    MenuItem createRemoveGarageSlotItem();
}

