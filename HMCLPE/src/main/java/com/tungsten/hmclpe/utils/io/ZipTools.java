package com.tungsten.hmclpe.utils.io;

import com.tungsten.hmclpe.utils.platform.OperatingSystem;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipTools {

    public static String readNormalMeta(String file,String targetName) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        ZipFile zf = new ZipFile(file, charset);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (!ze.isDirectory() && ze.getName().equals(targetName)) {
                InputStream inputStream = zf.getInputStream(ze);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();
                zin.closeEntry();
                return new String(bytes);
            }
        }
        zin.closeEntry();

        return "";
    }

    public static InputStream getFileInputStream(String file,String targetName) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        ZipFile zf = new ZipFile(file, charset);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (!ze.isDirectory() && ze.getName().equals(targetName)) {
                InputStream inputStream = zf.getInputStream(ze);
                zin.closeEntry();
                return inputStream;
            }
        }
        zin.closeEntry();

        return null;
    }

    public static boolean isFileExist(String file,String targetName) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (!ze.isDirectory() && ze.getName().equals(targetName)) {
                return true;
            }
        }
        zin.closeEntry();
        return false;
    }

    private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws IOException {
        if (file == null) {
            return;
        }

        // 如果是文件，则直接压缩该文件
        if (file.isFile()) {
            int count, bufferLen = 1024;
            byte data[] = new byte[bufferLen];

            // 获取文件相对于压缩文件夹根目录的子路径
            String subPath = file.getAbsolutePath();
            int index = subPath.indexOf(srcRootDir);
            if (index != -1) {
                subPath = subPath.substring(srcRootDir.length()
                        + File.separator.length());
            }
            ZipEntry entry = new ZipEntry(subPath);
            zos.putNextEntry(entry);
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            while ((count = bis.read(data, 0, bufferLen)) != -1) {
                zos.write(data, 0, count);
            }
            bis.close();
            zos.closeEntry();
        }
        // 如果是目录，则压缩整个目录
        else {
            // 压缩目录中的文件或子目录
            File[] childFileList = file.listFiles();
            for (int n = 0; n < childFileList.length; n++) {
                childFileList[n].getAbsolutePath().indexOf(
                        file.getAbsolutePath());
                zip(srcRootDir, childFileList[n], zos);
            }
        }
    }

    public static void zip(String srcPath, String zipPath, String zipFileName) throws IOException {
        if (isEmpty(srcPath) || isEmpty(zipPath) || isEmpty(zipFileName)) {
            throw new IOException("PARAMETER_IS_NULL");
        }
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            File srcFile = new File(srcPath);

            // 判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
            if (srcFile.isDirectory() && zipPath.contains(srcPath)) {
                throw new IOException("zipPath must not be the child directory of srcPath.");
            }

            // 判断压缩文件保存的路径是否存在，如果不存在，则创建目录
            File zipDir = new File(zipPath);
            if (!zipDir.exists() || !zipDir.isDirectory()) {
                zipDir.mkdirs();
            }

            // 创建压缩文件保存的文件对象
            String zipFilePath = zipPath + File.separator + zipFileName;
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                // 检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
                SecurityManager securityManager = new SecurityManager();
                securityManager.checkDelete(zipFilePath);
                // 删除已存在的目标文件
                zipFile.delete();
            }

            cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
            zos = new ZipOutputStream(cos);

            // 如果只是压缩一个文件，则需要截取该文件的父目录
            String srcRootDir = srcPath;
            if (srcFile.isFile()) {
                int index = srcPath.lastIndexOf(File.separator);
                if (index != -1) {
                    srcRootDir = srcPath.substring(0, index);
                }
            }
            // 调用递归压缩方法进行目录或文件压缩
            zip(srcRootDir, srcFile, zos);
            zos.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzipFile(String zipFilePath, String unzipFilePath, boolean includeZipFileName) throws IOException {
        unzipFile(zipFilePath,unzipFilePath,new ArrayList<>(),includeZipFileName);
    }

    @SuppressWarnings("unchecked")
    public static void unzipFile(String zipFilePath, String unzipFilePath, ArrayList<String> filter, boolean includeZipFileName) throws IOException {
        if (isEmpty(zipFilePath) || isEmpty(unzipFilePath)) {
            throw new IOException("PARAMETER_IS_NULL");
        }
        File zipFile = new File(zipFilePath);
        if (includeZipFileName) {
            String fileName = zipFile.getName();
            if (isNotEmpty(fileName)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            unzipFilePath = unzipFilePath + File.separator + fileName;
        }
        File unzipFileDir = new File(unzipFilePath);
        if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
            unzipFileDir.mkdirs();
        }
        ZipEntry entry = null;
        String entryFilePath = null, entryDirPath = null;
        File entryFile = null, entryDir = null;
        int index = 0, count = 0, bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();

            if (filter.contains(entry.getName())) {
                continue;
            }
            if (entry.isDirectory()) {
                continue;
            }
            entryFilePath = unzipFilePath + File.separator + entry.getName();
            index = Math.max(entryFilePath.lastIndexOf(File.separator), entryFilePath.lastIndexOf("/"));
            if (index != -1) {
                entryDirPath = entryFilePath.substring(0, index);
            } else {
                entryDirPath = "";
            }
            entryDir = new File(entryDirPath);
            if (!entryDir.exists() || !entryDir.isDirectory()) {
                entryDir.mkdirs();
            }
            entryFile = new File(entryFilePath);
            if (entryFile.exists()) {
                entryFile.delete();
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                bis = new BufferedInputStream(zip.getInputStream(entry));
                while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
                bos.close();
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.equals("");
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static org.apache.commons.compress.archivers.zip.ZipFile openZipFile(Path zipFile, Charset charset) throws IOException {
        return new org.apache.commons.compress.archivers.zip.ZipFile(Files.newByteChannel(zipFile), charset.name());
    }

    public static String readTextZipEntry(org.apache.commons.compress.archivers.zip.ZipFile zipFile, String name) throws IOException {
        return IOUtils.readFullyAsString(zipFile.getInputStream(zipFile.getEntry(name)), StandardCharsets.UTF_8);
    }

    public static Charset findSuitableEncoding(Path zipFile) throws IOException {
        return findSuitableEncoding(zipFile, Charset.availableCharsets().values());
    }

    public static Charset findSuitableEncoding(Path zipFile, Collection<Charset> candidates) throws IOException {
        try (org.apache.commons.compress.archivers.zip.ZipFile zf = openZipFile(zipFile, StandardCharsets.UTF_8)) {
            return findSuitableEncoding(zf, candidates);
        }
    }

    public static Charset findSuitableEncoding(org.apache.commons.compress.archivers.zip.ZipFile zipFile, Collection<Charset> candidates) throws IOException {
        if (testEncoding(zipFile, StandardCharsets.UTF_8)) return StandardCharsets.UTF_8;
        if (OperatingSystem.NATIVE_CHARSET != StandardCharsets.UTF_8 && testEncoding(zipFile, OperatingSystem.NATIVE_CHARSET))
            return OperatingSystem.NATIVE_CHARSET;

        for (Charset charset : candidates)
            if (charset != null && testEncoding(zipFile, charset))
                return charset;
        throw new IOException("Cannot find suitable encoding for the zip.");
    }

    private static CharsetDecoder newCharsetDecoder(Charset charset) {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    public static boolean testEncoding(Path zipFile, Charset encoding) throws IOException {
        try (org.apache.commons.compress.archivers.zip.ZipFile zf = openZipFile(zipFile, encoding)) {
            return testEncoding(zf, encoding);
        }
    }

    public static boolean testEncoding(org.apache.commons.compress.archivers.zip.ZipFile zipFile, Charset encoding) throws IOException {
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        CharsetDecoder cd = newCharsetDecoder(encoding);
        CharBuffer cb = CharBuffer.allocate(32);

        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();

            if (entry.getGeneralPurposeBit().usesUTF8ForNames()) continue;

            cd.reset();
            byte[] ba = entry.getRawName();
            int clen = (int)(ba.length * cd.maxCharsPerByte());
            if (clen == 0) continue;
            if (clen <= cb.capacity())
                cb.clear();
            else
                cb = CharBuffer.allocate(clen);

            ByteBuffer bb = ByteBuffer.wrap(ba, 0, ba.length);
            CoderResult cr = cd.decode(bb, cb, true);
            if (!cr.isUnderflow()) return false;
            cr = cd.flush(cb);
            if (!cr.isUnderflow()) return false;
        }
        return true;
    }

}
