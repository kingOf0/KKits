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

import com.kingOf0.kits.shade.smartinventory.event.abs.DisableEvent;
import com.kingOf0.kits.shade.smartinventory.event.abs.DisableEvent;
import com.kingOf0.kits.shade.smartinventory.event.abs.DisableEvent;
import com.kingOf0.kits.shade.smartinventory.InventoryContents;
import com.kingOf0.kits.shade.smartinventory.event.abs.DisableEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents plugin disable events.
 */
public final class PlgnDisableEvent implements DisableEvent {

  /**
   * the contents.
   */
  @NotNull
  private final InventoryContents contents;

  /**
   * the event.
   */
  @NotNull
  private final PluginDisableEvent event;

  public PlgnDisableEvent(@NotNull InventoryContents contents, @NotNull PluginDisableEvent event) {
    this.contents = contents;
    this.event = event;
  }

  @NotNull
  @Override
  public InventoryContents contents() {
    return this.contents;
  }

  @NotNull
  @Override
  public PluginDisableEvent getEvent() {
    return this.event;
  }
}
