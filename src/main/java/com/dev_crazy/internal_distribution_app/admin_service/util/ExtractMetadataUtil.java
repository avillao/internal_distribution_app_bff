package com.dev_crazy.internal_distribution_app.admin_service.util;

import com.android.tools.apk.analyzer.BinaryXmlParser;
import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import com.dev_crazy.internal_distribution_app.admin_service.model.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractMetadataUtil {

    private static final String ANDROID_NS =
            "http://schemas.android.com/apk/res/android";

    public static Metadata extractAndroidMetadata(InputStream inputStream) {
        try {
            ZipInputStream zipInputStream =
                    new ZipInputStream(inputStream);

            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("AndroidManifest.xml".equals(entry.getName())) {
                    ByteArrayOutputStream output =
                            new ByteArrayOutputStream();

                    zipInputStream.transferTo(output);

                    byte[] binaryManifest =
                            output.toByteArray();

                    byte[] decodedXml =
                            BinaryXmlParser.decodeXml(binaryManifest);

                    ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(decodedXml);

                    DocumentBuilderFactory factory =
                            DocumentBuilderFactory.newInstance();

                    DocumentBuilder builder =
                            factory.newDocumentBuilder();

                    Document document = builder.parse(xmlInputStream);

                    Element manifest =
                            document.getDocumentElement();

                    String packageName =
                            manifest.getAttribute("package");

                    String versionName =
                            manifest.getAttribute(
                                    "android:versionName"
                            );

                    return new Metadata(packageName, versionName);
                }
            }
        } catch (Exception e) {
            throw new BaseServiceException("Failed to decode binary XML via com.android.tools.apkparser", 500, e);
        }
        return null;
    }
}
