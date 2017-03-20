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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.SystemUtils;

/**
 * Utilidad para instarl la version indicada de adb dependiendo del so.
 *
 * @author Frost
 */
public class AdbInstaller {

    /**
     * Binarios de windows.
     */
    public static final String WIN_BIN = "platform-tools-latest-windows.zip";

    /**
     * Binarios de mac.
     */
    public static final String MAC_BIN = "platform-tools-latest-darwin.zip";

    /**
     * Binarios de linux.
     */
    public static final String UNX_BIN = "platform-tools-latest-linux.zip";

    /**
     * Desempaqueta los recursos de adb en el directorio especificado.
     *
     * @param resource recurso
     * @param destDir directorio
     * @throws InstallerException
     */
    protected void unpack(String resource, String destDir)
            throws InstallerException {
        InputStream resourceStream
                = getClass().getResourceAsStream("/" + resource);
        try (ZipInputStream inputStream = new ZipInputStream(resourceStream)) {
            ZipEntry entry = null;

            while (null != (entry = inputStream.getNextEntry())) {
                String entryName = entry.getName();
                File destFile = new File(destDir + File.separator + entryName);

                if (!entry.isDirectory()) {
                    try {
                        if (!destFile.getParentFile().exists()) {
                            destFile.getParentFile().mkdirs();
                        }
                        Files.copy(inputStream, destFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        throw new InstallerException(
                                String.format("cant save %s", entryName), ex);
                    }
                }
            }
        } catch (IOException ex) {
            throw new InstallerException(
                    String.format("Invalid resource %s", resource), ex);
        }
    }

    /**
     * Detecta el sistema operativo e instala los archivos de adb indicados.
     *
     * @param destDir directorio donde instalar adb
     */
    public void detectOsAndInstallAdb(String destDir) {
        String resource = UNX_BIN;
        if (SystemUtils.IS_OS_WINDOWS) {
            resource = WIN_BIN;
        } else if (SystemUtils.IS_OS_MAC) {
            resource = MAC_BIN;
        }

        unpack(resource, destDir);
    }

}
