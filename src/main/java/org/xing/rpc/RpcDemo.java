package org.xing.rpc;

import java.io.Serializable;

/**
 * Created by xingyuntian on 2018/3/11.
 */
public interface RpcDemo {
    Student getStudent(Integer id,String name);
    class Student implements Serializable{
        public long id;
        public String name;
        public int age;
        public boolean man;

        public Student(long id, String name, int age, boolean man) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.man = man;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isMan() {
            return man;
        }

        public void setMan(boolean man) {
            this.man = man;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", man=" + man +
                    '}';
        }
    }
}
