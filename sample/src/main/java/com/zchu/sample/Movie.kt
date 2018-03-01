package com.zchu.sample

class Movie {


    var count: Int = 0
    var start: Int = 0
    var title: String? = null
    var total: Int = 0
    var subjects: List<SubjectsBean>? = null

    class SubjectsBean {

        var alt: String? = null
        var collect_count: Int = 0
        var id: String? = null
        var images: ImagesBean? = null
        var original_title: String? = null
        var rating: RatingBean? = null
        var subtype: String? = null
        var title: String? = null
        var year: String? = null
        var casts: List<CastsBean>? = null
        var directors: List<DirectorsBean>? = null
        var genres: List<String>? = null


        override fun toString(): String {
            return "title='" + title + '\''.toString() +
                    ", year='" + year + '\''.toString()
        }

        class ImagesBean {
            var large: String? = null
            var medium: String? = null
            var small: String? = null
        }

        class RatingBean {

            var average: Double = 0.toDouble()
            var max: Int = 0
            var min: Int = 0
            var stars: String? = null
        }

        class CastsBean {

            var alt: String? = null
            var avatars: AvatarsBean? = null
            var id: String? = null
            var name: String? = null


        }

        class AvatarsBean {


            var large: String? = null
            var medium: String? = null
            var small: String? = null
        }

        class DirectorsBean {

            var alt: String? = null
            var avatars: AvatarsBeanX? = null
            var id: String? = null
            var name: String? = null


        }

        class AvatarsBeanX {

            var large: String? = null
            var medium: String? = null
            var small: String? = null
        }
    }


}
