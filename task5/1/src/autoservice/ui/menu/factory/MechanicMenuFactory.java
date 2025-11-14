package autoservice.ui.menu.factory;

import autoservice.ui.menu.MenuItem;

/**
 * Фабрика для создания пунктов меню, связанных с механиками.
 */
public interface MechanicMenuFactory {
    MenuItem createListMechanicsItem();
    MenuItem createAddMechanicItem();
    MenuItem createRemoveMechanicItem();
}

