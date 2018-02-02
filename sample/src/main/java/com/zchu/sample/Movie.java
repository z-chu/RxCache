package com.zchu.sample;

import java.util.List;

public class Movie {



    public int count;
    public int start;
    public String title;
    public int total;
    public List<SubjectsBean> subjects;

    public static class SubjectsBean {

        public String alt;
        public int collect_count;
        public String id;
        public ImagesBean images;
        public String original_title;
        public RatingBean rating;
        public String subtype;
        public String title;
        public String year;
        public List<CastsBean> casts;
        public List<DirectorsBean> directors;
        public List<String> genres;


        @Override
        public String toString() {
            return "title='" + title + '\'' +
                    ", year='" + year + '\'';
        }

        public static class ImagesBean {
            public String large;
            public String medium;
            public String small;
        }

        public static class RatingBean {

            public double average;
            public int max;
            public int min;
            public String stars;
        }

        public static class CastsBean {

            public String alt;
            public AvatarsBean avatars;
            public String id;
            public String name;


        }

        public static class AvatarsBean {


            public String large;
            public String medium;
            public String small;
        }

        public static class DirectorsBean {

            public String alt;
            public AvatarsBeanX avatars;
            public String id;
            public String name;


        }

        public static class AvatarsBeanX {

            public String large;
            public String medium;
            public String small;
        }
    }


}
