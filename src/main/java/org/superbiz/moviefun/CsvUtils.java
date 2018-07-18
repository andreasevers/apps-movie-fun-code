package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;

import javax.naming.NamingException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.apache.naming.ContextBindings.getClassLoader;

public class CsvUtils {

    public static String readFile(String path) {

        ClassLoader classLoader = CsvUtils.class.getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(file)).useDelimiter("\\A");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            return "";
        }
    }

    public static <T> List<T> readFromCsv(ObjectReader objectReader, String path) {
        try {
            List<T> results = new ArrayList<>();

            MappingIterator<T> iterator = objectReader.readValues(readFile(path));

            while (iterator.hasNext()) {
                results.add(iterator.nextValue());
            }

            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
