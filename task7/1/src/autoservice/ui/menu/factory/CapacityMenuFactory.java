package autoservice.ui.menu.factory;

import autoservice.ui.menu.MenuItem;

/**
 * Фабрика для создания пунктов меню, связанных с расчетом свободных слотов по времени.
 */
public interface CapacityMenuFactory {
    MenuItem createFreeCapacityAtItem();
    MenuItem createNextFreeDateItem();
}

