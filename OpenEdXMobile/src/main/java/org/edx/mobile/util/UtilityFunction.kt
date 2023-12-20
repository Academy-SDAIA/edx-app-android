
package org.edx.mobile.util


/**
 * In this class we will create all utility function that use all application.
 * @author Mirza Adil
 * @version 1.0
 */

object UtilityFunction {

    fun getEmployeeKey(employeeStatus : Any?):String{
        return when (employeeStatus) {
            "Public industry" -> { "PU" }
            "Private industry" -> { "PR" }
            "Job seeker" -> { "JS" }
            "Student" -> { "ST" }
            else->{""}
        }
    }

    fun getEducationKey(educationStatus : Any?):String{
        return when (educationStatus) {
            "Middle School" -> { "MS" }
            "High School" -> { "HS" }
            "Diploma" -> { "DM" }
            "Bachelor" -> { "BS" }
            "Master" -> { "MS" }
            "Ph.D." -> { "PH" }
            else->{""}
        }
    }

    fun getExperienceKey(experienceStatus : Any?):String{
        return when (experienceStatus) {
            "Junior level (0-2) years" -> { "JL" }
            "Middle level (3-4) years" -> { "ML" }
            "Senior level (5-10) years" -> { "SL" }
            "Expert (+ 10 years)" -> { "EL" }
            else->{""}
        }
    }

    fun getReginKey(reginStatus : String?):String{
        return when (reginStatus) {
            "Riyadh" -> { "RD" }
            "Eastern" -> { "ER" }
            "Asir" -> { "AI" }
            "Jazan" -> { "JA" }
            "Medina" -> { "MN" }
            "Al-Qassim" -> { "AS" }
            "Tabuk" -> { "TU" }
            "Ha'il" -> { "HI" }
            "Najran" -> { "NA" }
            "Al-Jawf" -> { "AW" }
            "Al-Bahah" -> { "AA" }
            "Northern Borders" -> { "NB" }
            else->{""}
        }
    }

    fun getGenderKey(genderStatus : Any?):String{
        return when (genderStatus) {
            "Male" -> { "m" }
            "Female" -> { "f" }
            else->{""}
        }
    }

    val reginList = arrayListOf<String>("Select Region","Riyadh","Eastern","Asir","Jazan","Medina","Al-Qassim","Tabuk","Ha'il","Najran","Al-Jawf","Al-Bahah","Northern Borders")
    val educationList = arrayListOf<String>("Select Education","Middle School","High School","Diploma","Bachelor","Master","Ph.D.")
    val englishLevelList = arrayListOf<String>("Select your english language level","0","1","2","3","4","5","6","7","8","9","10")
    val employmentList = arrayListOf<String>("Select your employment status","Public industry","Private industry","Job seeker","Student")
    val workExperienceList = arrayListOf<String>("Select work experience","Junior level (0-2) years","Middle level (3-4) years","Senior level (5-10) years","Expert (+ 10 years)")

}
