/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.kingOf0.kits.shade.smartinventory.event;

import com.kingOf0.kits.shade.smartinventory.event.abs.DragEvent;
import com.kingOf0.kits.shade.smartinventory.event.abs.DragEvent;
import com.kingOf0.kits.shade.smartinventory.event.abs.DragEvent;
import com.kingOf0.kits.shade.smartinventory.Icon;
import com.kingOf0.kits.shade.smartinventory.InventoryContents;
import com.kingOf0.kits.shade.smartinventory.event.abs.DragEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * a class that represents icon drag events.
 */
public final class IcDragEvent implements DragEvent {

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContents contents;

  /**
   * the event.
   */
  @NotNull
  private final InventoryDragEvent event;

  /**
   * the icon.
   */
  @NotNull
  private final Icon icon;

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  public IcDragEvent(@NotNull InventoryContents contents, @NotNull InventoryDragEvent event, @NotNull Icon icon, @NotNull Plugin plugin) {
    this.contents = contents;
    this.event = event;
    this.icon = icon;
    this.plugin = plugin;
  }

  @NotNull
  @Override
  public Map<Integer, ItemStack> added() {
    return this.event.getNewItems();
  }

  @NotNull
  @Override
  public DragType drag() {
    return this.event.getType();
  }

  @NotNull
  @Override
  public InventoryDragEvent getEvent() {
    return this.event;
  }

  @NotNull
  @Override
  public Optional<ItemStack> newCursor() {
    return Optional.ofNullable(this.event.getCursor());
  }

  @NotNull
  @Override
  public Set<Integer> slots() {
    return this.event.getInventorySlots();
  }

  @Override
  public void cancel() {
    this.event.setCancelled(true);
  }

  @Override
  public void close() {
    Bukkit.getScheduler().runTask(this.plugin, () ->
      this.contents.page().close(this.contents.player()));
  }

  @NotNull
  @Override
  public InventoryContents contents() {
    return this.contents;
  }

  @NotNull
  @Override
  public Icon icon() {
    return this.icon;
  }
}
