import com.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int employees;
        Scanner scan = new Scanner(System.in);

        Report report = new Report();

        System.out.println("Enter the number of Top performers threshold: ");
        report.setTopPerformersThreshold(scan.nextInt());
        scan.nextLine();

        String mplValue;
        do {
            System.out.println("Use experience multiplier? (Type - y for \"Yes\" or n for \"No\") ");
            mplValue = scan.next();
            scan.nextLine();

            if(mplValue.equals("y")){
                report.setUseExperienceMultiplier(true);
            } else {
                report.setUseExperienceMultiplier(false);
            }
        } while (!mplValue.equals("y") && !mplValue.equals("n"));


        System.out.println("Period limit: ");
        report.setPeriodLimit(scan.nextInt());
        scan.nextLine();


        do {
            System.out.println("Enter number of employees (1-10): ");
            employees = scan.nextInt();
        } while (employees < 1 || employees > 10);
        ArrayList<Employee> emp = new ArrayList<>();


        for (int i = 0; i < employees; i++) {
            Employee employee = new Employee();

            System.out.println("Enter a name for " + (i + 1) + " employee: ");

            employee.setName(scan.next());
            scan.nextLine();

            System.out.println("Enter total sales for " + (i + 1) + " employee: ");
            employee.setTotalSales(scan.nextInt());
            scan.nextLine();

            System.out.println("Enter sales period for " + (i + 1) + " employee: ");
            employee.setSalesPeriod(scan.nextInt());
            scan.nextLine();

            System.out.println("Enter an experience multiplier for " + (i + 1) + " employee: ");
            employee.setExperienceMultiplier(scan.nextDouble());
            scan.nextLine();

            if (report.isUseExperienceMultiplier()) {
                employee.setScore((employee.getTotalSales() / employee.getSalesPeriod()) * employee.getExperienceMultiplier());
            } else if (!report.isUseExperienceMultiplier()) {
                employee.setScore(employee.getTotalSales() / employee.getSalesPeriod());
            }

            emp.add(employee);
        }

        File file = new File("result.csv");

        try {
            FileWriter outputfile = new FileWriter(file);

            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            List<String[]> data = new ArrayList<>();

            JSONArray jsonData = new JSONArray();
            JSONObject jsonReport = new JSONObject();

            for (int i = 0; i < employees; i++) {
                Map m = new LinkedHashMap(employees);
                m.put("name", emp.get(i).getName());
                m.put("totalSales", emp.get(i).getTotalSales());
                m.put("salesPeriod", emp.get(i).getSalesPeriod());
                m.put("experienceMultiplier", emp.get(i).getExperienceMultiplier());
                jsonReport.put("topPerformersThreshold", report.getTopPerformersThreshold());
                jsonReport.put("useExperienceMultiplier", report.isUseExperienceMultiplier());
                jsonReport.put("periodLimit", report.getPeriodLimit());

                jsonData.add(m);

                PrintWriter printReport = new PrintWriter("Report.json");
                printReport.write(jsonReport.toJSONString());
                printReport.flush();
                printReport.close();

                PrintWriter printData = new PrintWriter("Data.json");
                printData.write(jsonData.toJSONString());
                printData.flush();
                printData.close();

                if (emp.get(i).getSalesPeriod() <= report.getPeriodLimit() && emp.get(i).getScore() <= report.getTopPerformersThreshold()) {
                    data.add(new String[]{emp.get(i).getName(), Double.toString(emp.get(i).getScore())});
                }
            }

            writer.writeAll(data);

            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
