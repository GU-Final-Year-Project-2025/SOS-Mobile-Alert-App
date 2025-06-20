package com.example.todoapp


data class ActivitiesScreenDetails(
    val date: String,
    val time: String,
    val location: String
)

data class PhoneContacts(val contactNumber: String)

val contactsList = listOf<PhoneContacts>(
    PhoneContacts("0780807525"),
    PhoneContacts("0734580647"),
    PhoneContacts("0734457433"),
    PhoneContacts("0778964565"),
    PhoneContacts("0712345567")
)

val activitiesList = listOf<ActivitiesScreenDetails>(
    ActivitiesScreenDetails("22/1/2025", "22:04", "Gulu City"),
    ActivitiesScreenDetails("23/1/2025", "10:15", "Kampala"),
    ActivitiesScreenDetails("24/1/2025", "14:30", "Entebbe"),
    ActivitiesScreenDetails("25/1/2025", "18:45", "Jinja"),
    ActivitiesScreenDetails("26/1/2025", "09:10", "Mbale"),
    ActivitiesScreenDetails("27/1/2025", "07:25", "Mbarara"),
    ActivitiesScreenDetails("28/1/2025", "16:50", "Arua"),
    ActivitiesScreenDetails("29/1/2025", "20:05", "Lira"),
    ActivitiesScreenDetails("30/1/2025", "11:20", "Masaka"),
    ActivitiesScreenDetails("31/1/2025", "15:35", "Hoima"),
    ActivitiesScreenDetails("1/2/2025", "08:45", "Soroti"),
    ActivitiesScreenDetails("2/2/2025", "12:10", "Fort Portal"),
    ActivitiesScreenDetails("3/2/2025", "17:55", "Tororo"),
    ActivitiesScreenDetails("4/2/2025", "19:40", "Kabale"),
    ActivitiesScreenDetails("5/2/2025", "21:30", "Gulu City"),
    ActivitiesScreenDetails("6/2/2025", "06:00", "Kampala"),
    ActivitiesScreenDetails("7/2/2025", "13:20", "Jinja"),
    ActivitiesScreenDetails("8/2/2025", "14:50", "Mbarara"),
    ActivitiesScreenDetails("9/2/2025", "23:10", "Lira"),
    ActivitiesScreenDetails("10/2/2025", "09:30", "Arua")
)