package software.ulpgc.kata2.io;

import software.ulpgc.kata2.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class RemoteMovieLoader implements MovieLoader {

    @Override
    public List<Movie> loadAll() {
        try {
            return loadFrom(new URL("https://datasets.imdbws.com/title.basics.tsv.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Movie> loadFrom(URL url) throws IOException {
        return loadFrom(url.openConnection());
    }

    private List<Movie> loadFrom(URLConnection connection) throws IOException {
        try (InputStream inputStream = unzip(connection.getInputStream())) {
            return loadFrom(inputStream);
        }
    }

    private List<Movie> loadFrom(InputStream is) throws IOException {
        return loadFrom(toReader(is));
    }

    private List<Movie> loadFrom(BufferedReader reader) throws IOException {
        List<Movie> movies =new ArrayList<>();
        MovieParser movieParser = new TsvMovieParser();
        reader.readLine();// Nos saltamos el header
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            movies.add(movieParser.parse(line));
        }
        return movies;

    }

    private BufferedReader toReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    private InputStream unzip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(inputStream));
    }
}