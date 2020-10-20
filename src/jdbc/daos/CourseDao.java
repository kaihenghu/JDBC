package jdbc.daos;

import jdbc.models.Course;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CourseDao {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String HOST = "localhost:";
    static final String SCHEMA = "database_design";
    static final String CONFIG = "serverTimezone=UTC";
    static final String DB_URL = "jdbc:mysql://" + HOST + "/" + SCHEMA + "?" + CONFIG;
    static final String USER = "dbDesign";
    static final String PASS = "dbDesign";

    static Connection connection = null;
    static PreparedStatement statement = null;
    Integer status = -1;

    public static Connection getConnection() {
        if (connection != null) return connection;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }

            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static final String FIND_ALL_COURSES = "SELECT * FROM courses";

    public List<Course> findAllCourses() {
        List<Course> courses = new ArrayList<>();
        connection = getConnection();
        try {
            statement = connection.prepareStatement(FIND_ALL_COURSES);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                Course course = new Course(id, title);
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return courses;
    }

    static final String UPDATE_COURSES = "UPDATE courses SET title=? WHERE id=?";

    public Integer updateCourse(Integer courseId, Course course) {
        connection = getConnection();
        try {
            statement = connection.prepareStatement(UPDATE_COURSES);
            statement.setString(1, course.getTitle());
            statement.setInt(2, course.getId());
            status = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return status;
    }

    static final String FIND_COURSE_BY_ID =
            "SELECT * FROM courses WHERE id=?";

    public Course findCourseById(Integer courseId) {
        connection = getConnection();
        try {
            statement = connection.prepareStatement(FIND_COURSE_BY_ID);
            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                Course course = new Course(courseId, title);
                return course;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return null;
    }

    static final String C​REATE_COURSE = "INSERT INTO courses VALUES (?,?)";

    public Integer createCourse(Course course) {
        connection = getConnection();
        try {
            statement = connection.prepareStatement(C​REATE_COURSE);
            statement.setInt(1, course.getId());
            statement.setString(2, course.getTitle());
            status = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return status;
    }

    public static void main(String[] args) {
        CourseDao dao = new CourseDao();
        System.out.println("*****************************");
        System.out.println("Find all courses");
        List<Course> courses = dao.findAllCourses();
        for (Course c : courses) {
            System.out.println(c);
        }
        System.out.println("*****************************");
        System.out.println("Update course 123 with title CS2222");
        Course course = new Course(123, "CS2222");
        Integer status = dao.updateCourse(123, course);
        courses = dao.findAllCourses();
        for (Course c : courses) {
            System.out.println(c);
        }
        System.out.println("*****************************");
        System.out.println("Find course by ID: 123");
        course = dao.findCourseById(123);
        System.out.println(course);

        System.out.println("*****************************");
        System.out.println("Create course 123 with title CS1234");
        course = new Course(345, "CS1234");
        status = dao.createCourse(course);
        System.out.println("create status : " + status);

        course = dao.findCourseById(345);
        System.out.println(course);
    }

}
