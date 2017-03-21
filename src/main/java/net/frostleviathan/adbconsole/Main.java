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

import java.io.File;
import java.util.List;

/**
 *
 * @author Frost
 */
public class Main {

    /**
     * Obtiene los archivos apk del directorio especificado.
     *
     * @param path directorio
     * @return archivos encontrados
     */
    public static File[] apkList(String path) {
        File currentDirectory = new File(System.getProperty("user.dir"));

        return currentDirectory.listFiles((File dir, String name) -> {
            return name.endsWith(".apk");
        });
    }

    public static void main(String... args) {
        String workingDir = System.getProperty("user.dir");

        AdbInstaller installer = new AdbInstaller();
        String adbPath = installer.detectOsAndInstallAdb(workingDir);

        AdbConsole console = new AdbConsole(adbPath);

        List<Device> devices = console.devices();
        devices.forEach((device) -> {
            System.out.printf("[%s]%s = %b" + System.lineSeparator(),
                    device.model(), device.deviceId(), device.isAuthorized());
        });

        if (!devices.isEmpty()) {
            Device device = devices.get(0);

            console.waitFor(device, (connectedDevice, connected) -> {
                System.out.printf("%s= %b" + System.lineSeparator(),
                        connectedDevice.deviceId(), connected);

                File[] apks = apkList(workingDir);

                console.install(connectedDevice, apks, true,
                        (targetDevice, files, success) -> {
                            System.out.printf("%s= installed %b" + System.lineSeparator(),
                                    targetDevice.deviceId(), success);
                        });
                console.cancel(connectedDevice);
            });
        }
    }

}
