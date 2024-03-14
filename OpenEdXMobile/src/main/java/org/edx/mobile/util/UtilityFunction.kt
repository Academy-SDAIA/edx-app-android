
package org.edx.mobile.util


/**
 * In this class we will create all utility function that use all application.
 * @author Mirza Adil
 * @version 1.0
 */

object UtilityFunction {

    fun getEmployeeKey(employeeStatus : Any?):String{
        return when (employeeStatus) {
            "Public industry","قطاع عام" -> { "PU" }
            "Private industry","قطاع خاص" -> { "PR" }
            "Job seeker","باحث عن عمل" -> { "JS" }
            "Student","طالب" -> { "ST" }
            else->{""}
        }
    }

    fun getEducationKey(educationStatus : Any?):String{
        return when (educationStatus) {
            "Middle School","المرحلة المتوسطة" -> { "MS" }
            "High School","المرحلة الثانوية" -> { "HS" }
            "Diploma","دبلوم" -> { "DM" }
            "Bachelor","بكالوريوس" -> { "BS" }
            "Master","ماجستير" -> { "MS" }
            "Ph.D.","دكتوراة" -> { "PH" }
            else->{""}
        }
    }

    fun getExperienceKey(experienceStatus : Any?):String{
        return when (experienceStatus) {
            "Junior level (0-2) years","المستوى المبتدئ (2-0) سنوات" -> { "JL" }
            "Middle level (3-4) years","المستوى المتوسط (4-3) سنوات" -> { "ML" }
            "Senior level (5-10) years","المستوى المتقدم (10-5) سنوات" -> { "SL" }
            "Expert (+ 10 years)","خبير (أكثر من  10+) سنوات" -> { "EL" }
            else->{""}
        }
    }

    fun getReginKey(reginStatus : String?):String{
        return when (reginStatus) {
            "Riyadh","الرياض" -> { "RD" }
            "Eastern","المنطقة الشرقية" -> { "ER" }
            "Asir","عسير" -> { "AI" }
            "Jazan","جازان" -> { "JA" }
            "Medina","المدينة المنورة" -> { "MN" }
            "Al-Qassim","القصيم" -> { "AS" }
            "Tabuk","تبوك" -> { "TU" }
            "Ha'il","حائل" -> { "HI" }
            "Najran","نجران" -> { "NA" }
            "Al-Jawf","الجوف" -> { "AW" }
            "Al-Bahah","الباحة" -> { "AA" }
            "Northern Borders","الحدود الشمالية" -> { "NB" }
            else->{""}
        }
    }

    fun getGenderKey(genderStatus : Any?):String{
        return when (genderStatus) {
            "Male","ذكر" -> { "m" }
            "Female","أنثى" -> { "f" }
            else->{""}
        }
    }

  //  val reginList = arrayListOf<String>("Select Region","Riyadh","Eastern","Asir","Jazan","Medina","Al-Qassim","Tabuk","Ha'il","Najran","Al-Jawf","Al-Bahah","Northern Borders")
   // val educationList = arrayListOf<String>("Select Education","Middle School","High School","Diploma","Bachelor","Master","Ph.D.")
   // val englishLevelList = arrayListOf<String>("Select your english language level","0","1","2","3","4","5","6","7","8","9","10")
    //val employmentList = arrayListOf<String>("Select your employment status","Public industry","Private industry","Job seeker","Student")
   // val workExperienceList = arrayListOf<String>("Select work experience","Junior level (0-2) years","Middle level (3-4) years","Senior level (5-10) years","Expert (+ 10 years)")

}
