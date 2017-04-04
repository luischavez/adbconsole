/*
 * Copyright (C) 2017 Frost
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.frostleviathan.adbconsole;

/**
 * Hilo que espera por el dispositivo.
 *
 * @author Frost
 */
public class WaitForThread extends Thread implements Runnable {

    /**
     * Estado del hilo.
     */
    private boolean alive = true;

    /**
     * Instancia de la consola.
     */
    private final Console console;

    /**
     * Dispositivo en espera.
     */
    private final Device device;

    /**
     * Proceso a ejecutar.
     */
    private final Process process;

    /**
     * Codigo a ejecutar al finalizar el proceso.
     */
    private final OnDeviceCallback callback;

    /**
     * Construye el hilo a partir de los valores especificados.
     *
     * @param console consola
     * @param device dispositivo
     * @param process proceso
     * @param callback codigo a ejecutar al finalizar
     */
    public WaitForThread(Console console, Device device, Process process,
            OnDeviceCallback callback) {
        this.console = console;
        this.device = device;
        this.process = process;
        this.callback = callback;
    }

    /**
     * Detiene la ejecucion del hilo.
     */
    public void stopWait() {
        alive = false;
    }

    @Override
    public void run() {
        Device updatedDevice = null;
        boolean connected = true;
        while (process.isAlive()) {
            updatedDevice = console.update(device);
            connected = null != updatedDevice;

            if (!alive || !connected) {
                process.destroy();
            }
        }

        console.stopWait(device);

        if (null != callback) {
            callback.onDevice(
                    null == updatedDevice ? device : updatedDevice, connected);
        }
    }

}
