package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.pagination.buttons.PageButton;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A specialized menu class that provides pagination functionality for displaying large sets of data.
 * <p>
 * This class extends the base Menu class and adds support for:
 * <ul>
 *   <li>Page navigation with previous/next buttons</li>
 *   <li>Reserving specific rows for navigation or other controls</li>
 *   <li>Automatic calculation of total pages based on content size</li>
 *   <li>Dynamic content distribution across available slots</li>
 * </ul>
 * </p>
 * <p>
 * To create a paginated menu, extend this class and implement the required abstract methods:
 * <ul>
 *   <li>{@link #getPrePaginatedTitle(Player)} - The base title for the menu</li>
 *   <li>{@link #getAllPagesButtons(Player)} - All buttons that should be paginated</li>
 *   <li>{@link #previousPageSlot(Player)} - The slot for the previous page button</li>
 *   <li>{@link #nextPageSlot(Player)} - The slot for the next page button</li>
 * </ul>
 * </p>
 * <p>
 * You can also optionally override:
 * <ul>
 *   <li>{@link #getGlobalButtons(Player)} - Buttons that appear on every page</li>
 *   <li>{@link #getMaxItemsPerPage(Player)} - Maximum number of items per page</li>
 * </ul>
 * </p>
 * 
 * @author ConaxGames
 * @version 2.0
 */
@Getter
public abstract class PaginatedMenu extends Menu {
    /**
     * The current page number (1-based indexing).
     */
    private int page = 1;
    
    /**
     * Set of row numbers that are reserved for navigation or other controls.
     * Rows are 0-based indexed (0-5 for a standard 6-row inventory).
     */
    private Set<Integer> reservedRows = new HashSet<>();

    /**
     * Sets which specific rows should be reserved for navigation or other controls.
     * <p>
     * Reserved rows will be skipped when placing content buttons, allowing you to
     * place navigation buttons or other controls in specific rows.
     * </p>
     * 
     * @param rows The row numbers to reserve (0-based indexing, where 0 is the top row)
     */
    public void setReservedRows(int... rows) {
        this.reservedRows.clear();
        for (int row : rows) {
            if (row >= 0 && row < 6) { // Valid rows are 0-5 (6 rows total)
                this.reservedRows.add(row);
            }
        }
    }

    /**
     * Gets the title for the menu, including the current page number.
     * 
     * @param player The player viewing the menu
     * @return The formatted title with page information
     */
    @Override
    public String getTitle(Player player) {
        return this.getPrePaginatedTitle(player) + " (" + this.page + "/" + this.getPages(player) + ")";
    }

    /**
     * Changes the current page by the specified amount and refreshes the menu.
     * 
     * @param player The player viewing the menu
     * @param mod The amount to change the page by (positive for next, negative for previous)
     */
    public final void modPage(Player player, int mod) {
        this.page += mod;
        this.getButtons().clear();
        this.openMenu(player, false);
    }

    /**
     * Checks if there is a next page available.
     * 
     * @param player The player viewing the menu
     * @return true if there is a next page, false otherwise
     */
    private boolean hasNext(Player player) {
        int pg = getPage() + 1;
        return pg > 0 && getPages(player) >= pg;
    }

    /**
     * Checks if there is a previous page available.
     * 
     * @param player The player viewing the menu
     * @return true if there is a previous page, false otherwise
     */
    private boolean hasPrevious(Player player) {
        int pg = getPage() + -1;
        return pg > 0 && getPages(player) >= pg;
    }

    /**
     * Calculates the total number of pages based on the number of buttons and items per page.
     * 
     * @param player The player viewing the menu
     * @return The total number of pages
     */
    public final int getPages(Player player) {
        int buttonAmount = this.getAllPagesButtons(player).size();
        if (buttonAmount == 0) {
            return 1;
        }
        return (int)Math.ceil((double)buttonAmount / (double)this.getMaxItemsPerPage(player));
    }

    /**
     * Gets all buttons for the current page, including navigation buttons and content.
     * <p>
     * This method handles:
     * <ul>
     *   <li>Placing navigation buttons in the specified slots</li>
     *   <li>Distributing content buttons across available slots, skipping reserved rows</li>
     *   <li>Adding global buttons that appear on every page</li>
     * </ul>
     * </p>
     * 
     * @param player The player viewing the menu
     * @return A map of slot positions to buttons
     */
    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int)((double)(this.page - 1) * (double)this.getMaxItemsPerPage(player));
        int maxIndex = (int)((double)this.page * (double)this.getMaxItemsPerPage(player));

        int previousSlot = this.previousPageSlot(player);
        int nextSlot = this.nextPageSlot(player);

        HashMap<Integer, Button> buttons = new HashMap<>();

        if (hasPrevious(player)) {
            buttons.put(previousSlot, new PageButton(-1, this));
        }
        if (hasNext(player)) {
            buttons.put(nextSlot, new PageButton(1, this));
        }

        for (Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();
            if (ind < minIndex || ind >= maxIndex) continue;
            
            // Calculate the target slot, skipping reserved rows
            int targetSlot = ind - (int)((double)this.getMaxItemsPerPage(player) * (double)(this.page - 1));
            int currentRow = targetSlot / 9;
            int currentCol = targetSlot % 9;
            
            // Count how many reserved rows we need to skip before this slot
            int skippedRows = 0;
            for (int reservedRow : this.reservedRows) {
                if (reservedRow < currentRow) {
                    skippedRows++;
                }
            }
            
            // Adjust the slot by skipping reserved rows
            targetSlot = (currentRow - skippedRows) * 9 + currentCol;
            
            buttons.put(targetSlot, entry.getValue());
        }

        Map<Integer, Button> global = this.getGlobalButtons(player);
        if (global != null) {
            for (Map.Entry<Integer, Button> gent : global.entrySet()) {
                buttons.put(gent.getKey(), gent.getValue());
            }
        }

        return buttons;
    }

    /**
     * Gets the maximum number of items that can be displayed per page.
     * <p>
     * This value is calculated based on the inventory size and the number of reserved rows.
     * </p>
     * 
     * @param player The player viewing the menu
     * @return The maximum number of items per page
     */
    public int getMaxItemsPerPage(Player player) {
        return 54 - (this.reservedRows.size() * 9); // Adjust max items based on number of reserved rows
    }

    /**
     * Gets buttons that should appear on every page, regardless of pagination.
     * <p>
     * These buttons are added after the paginated content, so they will appear on top of
     * any content that might be placed in the same slots.
     * </p>
     * 
     * @param player The player viewing the menu
     * @return A map of slot positions to buttons, or null if there are no global buttons
     */
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    /**
     * Gets the base title for the menu, before page information is added.
     * 
     * @param player The player viewing the menu
     * @return The base title
     */
    public abstract String getPrePaginatedTitle(Player var1);

    /**
     * Gets all buttons that should be paginated.
     * <p>
     * These buttons will be distributed across pages based on the maximum items per page.
     * </p>
     * 
     * @param player The player viewing the menu
     * @return A map of slot positions to buttons
     */
    public abstract Map<Integer, Button> getAllPagesButtons(Player var1);

    /**
     * Gets the slot where the previous page button should be placed.
     * 
     * @param player The player viewing the menu
     * @return The slot for the previous page button
     */
    public abstract int previousPageSlot(Player var1);
    
    /**
     * Gets the slot where the next page button should be placed.
     * 
     * @param player The player viewing the menu
     * @return The slot for the next page button
     */
    public abstract int nextPageSlot(Player var1);
}

