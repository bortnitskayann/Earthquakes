package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.persistence.Query;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Tasks {
    private Date convertDate(String str) {
        str = str.replace("T", " ");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = parser.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = formatter.format(date);
        try {
            date = formatter.parse(formattedDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }
    public void loadData(){
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Earthquake.class)
                .addAnnotatedClass(MagnitudeType.class)
                .buildSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            FileReader filereader = new FileReader("Землетрясения.csv");
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            String[] nextRecord;
            session.beginTransaction();
            Set<String> existingMagnitudeType = new HashSet<>();

            while ((nextRecord = csvReader.readNext()) != null) {
                String magnitudeType = nextRecord[2];
                MagnitudeType magnitude = null;
                if (existingMagnitudeType.contains(magnitudeType)) {
                    Query query = session.createQuery("from MagnitudeType where magnitudeType = :type", MagnitudeType.class);
                    query.setParameter("type", magnitudeType);
                    List<MagnitudeType> existingMagnitudeList = query.getResultList();
                    if (!existingMagnitudeList.isEmpty()) {
                        magnitude = existingMagnitudeList.get(0);
                    }
                } else {
                    magnitude = new MagnitudeType(nextRecord[2]);
                    existingMagnitudeType.add(magnitudeType);
                }
                Earthquake earthquake = new Earthquake(nextRecord[0], Integer.parseInt(nextRecord[1]),
                        magnitude, Float.parseFloat(nextRecord[3]), nextRecord[4], convertDate(nextRecord[5]));
                session.save(magnitude);
                session.save(earthquake);
            }
            session.getTransaction().commit();
            factory.close();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        } finally {
            factory.close();
        }
    }

    public void graphic(){
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Earthquake.class)
                .addAnnotatedClass(MagnitudeType.class)
                .buildSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            List<Object[]> results = session.createQuery("select YEAR(e.dateTime), COUNT(e.id) AS valueCount " +
                            "from Earthquake e " +
                            "GROUP BY YEAR(e.dateTime)", Object[].class)
                    .getResultList();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Object[] res : results) {
                int year = (Integer) res[0];
                Number value = (Number) res[1];
                dataset.addValue(value, "Количество землетрясений за год", year);
            }
            JFreeChart chart = ChartFactory.createLineChart(
                    "Землетрясения по годам",
                    "Года",
                    "Количество землетрясений",
                    dataset);
            CategoryPlot p = chart.getCategoryPlot();
            p.setRangeGridlinePaint(Color.BLACK);
            ChartFrame frame = new ChartFrame("Bar chart", chart);
            frame.setVisible(true);
            frame.setSize(800, 400);
        } finally {
            factory.close();
        }
    }

    public void avgMagnitude(){
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Earthquake.class)
                .addAnnotatedClass(MagnitudeType.class)
                .buildSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            List<Double> result = session.createQuery("select ROUND(AVG(e.magnitude), 2) as avgMagnitude " +
                            "from Earthquake e " +
                            "where e.state = 'West Virginia'", Double.class)
                    .getResultList();
            if (!result.isEmpty()) {
                System.out.println("Средняя магнитуда " + result.get(0));
            }
        } finally {
            factory.close();
        }
    }

    public void deepestEarthquake(){
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Earthquake.class)
                .addAnnotatedClass(MagnitudeType.class)
                .buildSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            List<Object[]> result = session.createQuery("select e.state, MAX(e.depth) AS maxDepth " +
                            "from Earthquake e " +
                            "where YEAR(e.dateTime) = 2013 " +
                            "GROUP BY e.state " +
                            "ORDER BY maxDepth DESC", Object[].class)
                    .getResultList();
            if (!result.isEmpty()) {
                Object[] res = result.get(0);
                String state = (String) res[0];
                int maxMagnitude = (int) res[1];
                System.out.println("Штат, в котором произошло самое глубокое землетрясение: " + state + ", магнитуда: " + maxMagnitude);
            }
        } finally {
            factory.close();
        }
    }
}
