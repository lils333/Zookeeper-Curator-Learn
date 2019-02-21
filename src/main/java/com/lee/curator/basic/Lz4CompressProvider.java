package com.lee.curator.basic;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.curator.framework.api.CompressionProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Lz4CompressProvider implements CompressionProvider {
    @Override
    public byte[] compress(String path, byte[] data) throws Exception {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        try (CompressorOutputStream stream =
                     new CompressorStreamFactory().createCompressorOutputStream(
                             CompressorStreamFactory.LZ4_FRAMED, byteArray)) {
            stream.write(data);
        }
        return byteArray.toByteArray();
    }

    @Override
    public byte[] decompress(String path, byte[] compressedData) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(compressedData.length);
        try (CompressorInputStream stream
                     = new CompressorStreamFactory().createCompressorInputStream(
                CompressorStreamFactory.LZ4_FRAMED, new ByteArrayInputStream(compressedData))) {
            IOUtils.copy(stream, bytes);
        }
        return bytes.toByteArray();
    }
}
