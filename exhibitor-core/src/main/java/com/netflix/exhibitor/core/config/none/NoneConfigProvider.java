/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.exhibitor.core.config.none;

import com.google.common.io.Closeables;
import com.netflix.exhibitor.core.config.ConfigCollection;
import com.netflix.exhibitor.core.config.ConfigProvider;
import com.netflix.exhibitor.core.config.LoadedInstanceConfig;
import com.netflix.exhibitor.core.config.PropertyBasedInstanceConfig;
import com.netflix.exhibitor.core.config.PseudoLock;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

// TODO

public class NoneConfigProvider implements ConfigProvider
{
    private final File directory;

    private static final String     FILE_NAME = "exhibitor.properties";

    public NoneConfigProvider(String directory)
    {
        this.directory = new File(directory);
    }

    @Override
    public void start() throws Exception
    {
        // NOP
    }

    @Override
    public void close() throws IOException
    {
        // NOP
    }

    @Override
    public LoadedInstanceConfig loadConfig() throws Exception
    {
        File            propertiesFile = new File(directory, FILE_NAME);
        Properties      properties = new Properties();
        if ( propertiesFile.exists() )
        {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(propertiesFile));
            try
            {
                properties.load(in);
            }
            finally
            {
                Closeables.closeQuietly(in);
            }
        }
        PropertyBasedInstanceConfig config = new PropertyBasedInstanceConfig(properties, new Properties());
        return new LoadedInstanceConfig(config, propertiesFile.lastModified());
    }

    @Override
    public LoadedInstanceConfig storeConfig(ConfigCollection config, long compareLastModified) throws Exception
    {
        File                            propertiesFile = new File(directory, FILE_NAME);
        PropertyBasedInstanceConfig     propertyBasedInstanceConfig = new PropertyBasedInstanceConfig(config);

        long                            lastModified = 0;
        OutputStream                    out = new BufferedOutputStream(new FileOutputStream(propertiesFile));
        try
        {
            propertyBasedInstanceConfig.getProperties().store(out, "Auto-generated by Exhibitor");
            lastModified = propertiesFile.lastModified();
        }
        finally
        {
            Closeables.closeQuietly(out);
        }

        return new LoadedInstanceConfig(propertyBasedInstanceConfig, lastModified);
    }

    @Override
    public void writeInstanceHeartbeat() throws Exception
    {
        // NOP
    }

    @Override
    public boolean isHeartbeatAliveForInstance(String instanceHostname, int deadInstancePeriodMs) throws Exception
    {
        return true;
    }

    @Override
    public void clearInstanceHeartbeat() throws Exception
    {
        // NOP
    }

    @Override
    public PseudoLock newPseudoLock() throws Exception
    {
        return new PseudoLock()
        {
            @Override
            public void lock() throws Exception
            {
                // NOP
            }

            @Override
            public boolean lock(long maxWait, TimeUnit unit) throws Exception
            {
                return true;
            }

            @Override
            public void unlock() throws Exception
            {
                // NOP
            }
        };
    }
}