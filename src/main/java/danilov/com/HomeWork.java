package danilov.com;

import com.github.javafaker.Faker;

import java.util.Random;

import java.sql.*;
import java.util.Scanner;

public class HomeWork {

    public static String getRandomSex() {
        Random random = new Random();
        boolean isMale = random.nextBoolean();

        if (isMale) {
            return "M";
        } else {
            return "F";
        }
    }

    public String getRandomGroup() {
        Random random = new Random();
        int intGroup = random.nextInt(3) + 1;
        String Group = Integer.toString(intGroup);
        return Group;
    }

    public String getRandomName() {
        Faker faker = new Faker();
        String randomName = faker.name().fullName();
        return randomName;
    }

    public String getRandomGroupName() {
        Faker faker = new Faker();
        String randomName = faker.name().title();
        return randomName;
    }

    public String getRandomCurator() {
        Random random = new Random();
        int intCurator = random.nextInt(4) + 1;
        String Curator = Integer.toString(intCurator);
        return Curator;
    }

    private static final String CONNECTION_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwe123";

    private static final String CREATE_CURATOR_SQL =
            "CREATE TABLE curator (id BIGSERIAL, fio varchar(50), PRIMARY KEY (id))";
    private static final String CREATE_GROUP_SQL =
            "CREATE TABLE groupp (id BIGSERIAL, name varchar(50),id_curator integer, PRIMARY KEY (id))";
    private static final String CREATE_STUDENT_SQL =
            "CREATE TABLE student (id BIGSERIAL, fio varchar(50), sex varchar (10), id_group integer, PRIMARY KEY (id))";

    private String insertRandomCurator() {
        return "INSERT INTO curator(fio) VALUES('" + getRandomName() + "')";
    }

    private String insertRandomStudent() {
        return "INSERT INTO student(fio,sex,id_group) VALUES('" + getRandomName() + "','" + getRandomSex() + "','" + getRandomGroup() + "')";
    }

    private String insertRandomGroup() {
        return "INSERT INTO groupp(name,id_curator) VALUES ('" + getRandomGroupName() + "','" + getRandomCurator() + "')";
    }

    private static final String SELECT_STUDENT =
            "select student.fio, groupp.name, curator.fio from student " +
                    "JOIN groupp on student.id_group = groupp.id " +
                    "JOIN curator on groupp.id_curator = curator.id";

    private static final String SELECT_GROUP =
            "select groupp.name, curator.fio from groupp JOIN curator on groupp.id_curator = curator.id";


    public void createCuratorTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.execute(CREATE_CURATOR_SQL);
        }
    }

    public void createGroupTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.execute(CREATE_GROUP_SQL);
        }
    }

    public void createStudentTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.execute(CREATE_STUDENT_SQL);
        }
    }

    public void insertIntoCurator(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(insertRandomCurator());
        }
    }

    public void insertIntoStudent(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(insertRandomStudent());
        }
    }

    public void insertIntoGroup(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(insertRandomGroup());
        }
    }

    public void selectStudent(Connection connection) throws SQLException {
        System.out.println("Список всех студентов:");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_STUDENT);) {
            while (resultSet.next()) {
                String studentName = resultSet.getString(1);
                String groupName = resultSet.getString(2);
                String curatorName = resultSet.getString(3);
                String row = String.format("Студент: %s, Название группы: %s, имя куратора: %s", studentName, groupName, curatorName);
                System.out.println(row);
            }
            System.out.println("");
        }
    }

    public void selectCountStudent(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) from STUDENT");) {
            while (resultSet.next()) {
                String count = resultSet.getString(1);
                String row = String.format("Количество студентов: %s", count);
                System.out.println(row);
            }
            System.out.println("");
        }
    }

    public void selectFemaleStudent(Connection connection) throws SQLException {
        System.out.println("Список всех студенток:");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT fio from STUDENT where sex = 'F'");) {
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                System.out.println(name);
            }
            System.out.println("");
        }
    }

    public void updateCuratorIntoGroup(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE groupp SET id_curator = ? where id = ?");) {
            int curator = Integer.parseInt(getRandomCurator());
            int group = Integer.parseInt(getRandomGroup());
            statement.setInt(1, curator);
            statement.setInt(2, group);
            statement.executeUpdate();
            System.out.println("В группе id = " + group + " изменился куратор на id = " + curator);
            System.out.println("");
        }
    }

    public void selectGroup(Connection connection) throws SQLException {
        System.out.println("Список групп и их кураторов:");
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_GROUP);) {
            while (resultSet.next()) {
                String groupName = resultSet.getString(1);
                String curatorName = resultSet.getString(2);
                String row = String.format("Название группы: %s, имя куратора: %s", groupName, curatorName);
                System.out.println(row);
            }
            System.out.println("");
        }
    }

    public void selectFindStudent(Connection connection) throws SQLException {
        System.out.println("Введите название группы для которой требуется вывести список студентов:");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        System.out.println("Список студентов группы '" + input + "':");
        String SELECT_FIND_STUDENT =
                "select student.fio from student" +
                        " JOIN groupp on student.id_group = groupp.id where groupp.name = '" + input + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_FIND_STUDENT);) {
            while (resultSet.next()) {
                String studentName = resultSet.getString(1);
                String row = String.format("%s", studentName);
                System.out.println(row);
            }
            System.out.println("");
        }
    }


    public static void main(String[] args) throws SQLException {

        HomeWork homeWork = new HomeWork();
        try (Connection connection = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            homeWork.createCuratorTable(connection);
            homeWork.createGroupTable(connection);
            homeWork.createStudentTable(connection);
            for (int i = 0; i < 4; i++) {
                homeWork.insertIntoCurator(connection);
            }
            for (int i = 0; i < 15; i++) {
                homeWork.insertIntoStudent(connection);
            }
            for (int i = 0; i < 3; i++) {
                homeWork.insertIntoGroup(connection);
            }
            homeWork.selectStudent(connection);
            homeWork.selectCountStudent(connection);
            homeWork.selectFemaleStudent(connection);
            homeWork.updateCuratorIntoGroup(connection);
            homeWork.selectGroup(connection);
            homeWork.selectFindStudent(connection);


        }


    }
}