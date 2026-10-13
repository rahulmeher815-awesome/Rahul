package com.example.data.local

import java.util.UUID

object SeedData {

    val defaultSettings = PlatformSettingsEntity(
        id = 1,
        farmerFee = 30.0,
        expertFee = 30.0,
        isDisputeProtectionActive = true
    )

    val userFarmer = UserEntity(
        id = "farmer_1",
        name = "Ramesh Kumar",
        email = "ramesh.farmer@agriconnect.in",
        phone = "+91 98765 43210",
        role = "FARMER",
        location = "Karnal, Haryana",
        avatarInitials = "RK"
    )

    val userAdmin = UserEntity(
        id = "admin_1",
        name = "Pooja Verma (Admin)",
        email = "admin@agriconnect.in",
        phone = "+91 91234 56789",
        role = "ADMIN",
        location = "New Delhi",
        avatarInitials = "PV"
    )

    val expertUsers = listOf(
        UserEntity(
            id = "expert_1",
            name = "Dr. Rahul Sharma",
            email = "rahul.sharma@agriconnect.in",
            phone = "+91 98111 22334",
            role = "EXPERT",
            location = "Ludhiana, Punjab",
            avatarInitials = "RS"
        ),
        UserEntity(
            id = "expert_2",
            name = "Dr. Sunita Patel",
            email = "sunita.patel@agriconnect.in",
            phone = "+91 98222 33445",
            role = "EXPERT",
            location = "Ahmedabad, Gujarat",
            avatarInitials = "SP"
        ),
        UserEntity(
            id = "expert_3",
            name = "Prof. K. Venkatraman",
            email = "k.venkatraman@agriconnect.in",
            phone = "+91 98333 44556",
            role = "EXPERT",
            location = "Coimbatore, Tamil Nadu",
            avatarInitials = "KV"
        ),
        UserEntity(
            id = "expert_4",
            name = "Dr. Ananya Mukherjee",
            email = "ananya.m@agriconnect.in",
            phone = "+91 98444 55667",
            role = "EXPERT",
            location = "Kolkata, West Bengal",
            avatarInitials = "AM"
        ),
        UserEntity(
            id = "expert_5",
            name = "Er. Manpreet S. Dhillon",
            email = "manpreet.d@agriconnect.in",
            phone = "+91 98555 66778",
            role = "EXPERT",
            location = "Bathinda, Punjab",
            avatarInitials = "MD"
        ),
        UserEntity(
            id = "expert_6",
            name = "Dr. Rajeshwar Deshmukh",
            email = "rajeshwar.d@agriconnect.in",
            phone = "+91 98666 77889",
            role = "EXPERT",
            location = "Nagpur, Maharashtra",
            avatarInitials = "RD"
        )
    )

    val expertProfiles = listOf(
        ExpertProfileEntity(
            expertId = "expert_1",
            title = "Plant Pathology & Disease Specialist",
            qualification = "Ph.D. in Plant Pathology (ICAR-IARI)",
            institution = "Indian Agricultural Research Institute",
            experienceYears = 12,
            bio = "Specialist in identifying fungal, bacterial and viral infections in cereal crops and vegetables. Practical IPM recommendations with safe dosage guidelines.",
            pricePerConsultation = 299.0,
            rating = 4.9,
            reviewCount = 48,
            consultationCount = 184,
            isVerified = true,
            verificationDocName = "ICAR_PhD_Certificate_Verif.pdf",
            languagesSpoken = "Hindi, English, Punjabi",
            specializedCrops = "Paddy, Wheat, Tomato, Potato, Mustard",
            expertiseCategories = "Plant Pathology (Diseases), General Crop Protection, Organic Farming"
        ),
        ExpertProfileEntity(
            expertId = "expert_2",
            title = "Entomologist & Pest Control Advisor",
            qualification = "Ph.D. in Agricultural Entomology (PAU)",
            institution = "Punjab Agricultural University",
            experienceYears = 9,
            bio = "Expert in pink bollworm, fall armyworm, whiteflies, and bio-pesticides. Focused on minimum chemical residue and maximum crop yield preservation.",
            pricePerConsultation = 249.0,
            rating = 4.8,
            reviewCount = 36,
            consultationCount = 122,
            isVerified = true,
            verificationDocName = "PAU_Entomology_Degree.pdf",
            languagesSpoken = "Hindi, English, Gujarati",
            specializedCrops = "Cotton, Paddy, Sugarcane, Chilli, Maize",
            expertiseCategories = "Entomology (Pest & Insects), General Crop Protection"
        ),
        ExpertProfileEntity(
            expertId = "expert_3",
            title = "Soil Fertility & Nutrient Specialist",
            qualification = "M.Sc. (Agri) Soil Science (TNAU)",
            institution = "Tamil Nadu Agricultural University",
            experienceYears = 15,
            bio = "Restoring depleted soils, correcting micronutrient deficiencies (Zinc, Boron, Iron), saline-alkali land reclamation, and organic carbon management.",
            pricePerConsultation = 349.0,
            rating = 4.9,
            reviewCount = 52,
            consultationCount = 210,
            isVerified = true,
            verificationDocName = "TNAU_SoilScience_MSc.pdf",
            languagesSpoken = "English, Tamil, Telugu, Hindi",
            specializedCrops = "Paddy, Banana, Coconut, Sugarcane, Pulses",
            expertiseCategories = "Soil Science & Fertilizers, Organic Farming, Irrigation & Water Tech"
        ),
        ExpertProfileEntity(
            expertId = "expert_4",
            title = "Horticulture & Vegetable Specialist",
            qualification = "Ph.D. in Horticulture (BCKV)",
            institution = "Bidhan Chandra Krishi Viswavidyalaya",
            experienceYears = 8,
            bio = "Vegetable diseases, polyhouse cultivation, nursery management, and tomato leaf curl virus recovery protocols.",
            pricePerConsultation = 199.0,
            rating = 4.7,
            reviewCount = 29,
            consultationCount = 95,
            isVerified = true,
            verificationDocName = "BCKV_Horticulture_PhD.pdf",
            languagesSpoken = "Bengali, Hindi, English",
            specializedCrops = "Tomato, Brinjal, Onion, Cauliflower, Mango",
            expertiseCategories = "Horticulture & Fruits/Vegetables, Plant Pathology (Diseases), Irrigation & Water Tech"
        ),
        ExpertProfileEntity(
            expertId = "expert_5",
            title = "Precision Agri & Irrigation Engineer",
            qualification = "M.Tech in Agricultural Engineering (IIT KGP)",
            institution = "AgriTech Innovation Council",
            experienceYears = 7,
            bio = "Drip fertigation design, soil moisture scheduling, weed management timing, and mechanization advice.",
            pricePerConsultation = 299.0,
            rating = 4.8,
            reviewCount = 21,
            consultationCount = 64,
            isVerified = true,
            verificationDocName = "IIT_KGP_MTech_AgriEngg.pdf",
            languagesSpoken = "Punjabi, Hindi, English",
            specializedCrops = "Wheat, Cotton, Maize, Kinnow, Soybean",
            expertiseCategories = "Irrigation & Water Tech, Weed Management, Agronomy & Crop Management"
        ),
        ExpertProfileEntity(
            expertId = "expert_6",
            title = "Organic Farming & Bio-Input Consultant",
            qualification = "M.Sc. in Agronomy (VNMKV)",
            institution = "Maharashtra Agro-Ecology Network",
            experienceYears = 6,
            bio = "Chemical-free sustainable farming, Jeevamrutha preparation, bio-control agent culturing, and soil microbial revival.",
            pricePerConsultation = 199.0,
            rating = 4.6,
            reviewCount = 14,
            consultationCount = 38,
            isVerified = false, // Unverified - demonstrates verification flow in Admin!
            verificationDocName = "VNMKV_Agronomy_Degree.pdf",
            languagesSpoken = "Marathi, Hindi, English",
            specializedCrops = "Soybean, Cotton, Gram, Pigeon Pea, Sugarcane",
            expertiseCategories = "Organic Farming, Agronomy & Crop Management, Soil Science & Fertilizers"
        )
    )

    fun generateInitialSlots(): List<AvailabilitySlotEntity> {
        val slots = mutableListOf<AvailabilitySlotEntity>()
        val timeSlots = listOf(
            "10:00 AM – 11:00 AM",
            "11:30 AM – 12:30 PM",
            "03:00 PM – 04:00 PM",
            "05:00 PM – 06:00 PM"
        )
        expertProfiles.forEach { expert ->
            listOf("Today", "Tomorrow", "Thursday", "Friday").forEachIndexed { dayIdx, dayLabel ->
                val dateString = when (dayIdx) {
                    0 -> "14 Oct 2026"
                    1 -> "15 Oct 2026"
                    2 -> "16 Oct 2026"
                    else -> "17 Oct 2026"
                }
                timeSlots.forEachIndexed { tIdx, timeSlot ->
                    slots.add(
                        AvailabilitySlotEntity(
                            id = "slot_${expert.expertId}_${dayIdx}_$tIdx",
                            expertId = expert.expertId,
                            dayLabel = dayLabel,
                            dateString = dateString,
                            timeSlot = timeSlot,
                            isBooked = false
                        )
                    )
                }
            }
        }
        return slots
    }

    val sampleProblems = listOf(
        ProblemEntity(
            id = "prob_1",
            farmerId = "farmer_1",
            crop = "Paddy (Rice)",
            cropVariety = "Pusa Basmati 1121",
            location = "Karnal, Haryana",
            category = "Plant Pathology (Diseases)",
            description = "My paddy leaves are turning yellow and spindle-shaped brown lesions are spreading rapidly from the tip down to the sheath.",
            startedWhen = "4 days ago",
            symptoms = "Yellowing leaves, brown spindle lesions with grey center, premature leaf drying",
            previousTreatment = "Sprayed Urea (10kg/acre) thinking it was nitrogen deficiency, but spots spread further.",
            urgency = "High",
            status = "IN_CONSULTATION",
            createdAt = System.currentTimeMillis() - 86400000 * 2,
            imageUri = "sample_paddy_blast"
        ),
        ProblemEntity(
            id = "prob_2",
            farmerId = "farmer_1",
            crop = "Tomato",
            cropVariety = "Abhinav F1",
            location = "Karnal, Haryana",
            category = "Horticulture & Fruits/Vegetables",
            description = "Upper leaves are curling inward and turning pale yellow. Plant growth seems stunted.",
            startedWhen = "1 week ago",
            symptoms = "Upward leaf curling, stunted terminal shoots, white small flies flying around plants",
            previousTreatment = "None yet",
            urgency = "Medium",
            status = "OPEN",
            createdAt = System.currentTimeMillis() - 86400000 * 1,
            imageUri = "sample_tomato_curl"
        )
    )

    val sampleBooking = BookingEntity(
        id = "book_1",
        problemId = "prob_1",
        farmerId = "farmer_1",
        expertId = "expert_1",
        consultationType = "Chat Consultation",
        slotDate = "14 Oct 2026",
        slotTime = "10:00 AM – 11:00 AM",
        consultationPrice = 299.0,
        farmerPlatformFee = 30.0,
        totalFarmerPaid = 329.0,
        expertPlatformFee = 30.0,
        expertEarnings = 269.0,
        platformRevenue = 60.0,
        status = "CONFIRMED",
        bookedAt = System.currentTimeMillis() - 3600000 * 4
    )

    val sampleMessages = listOf(
        MessageEntity(
            id = "msg_1",
            bookingId = "book_1",
            senderId = "farmer_1",
            senderRole = "FARMER",
            text = "Namaste Dr. Rahul Sharma. My Basmati paddy leaves have spindle-shaped lesions. I uploaded photos in the problem description.",
            attachmentDescription = "Photo: Spindle-shaped lesions on leaves",
            timestamp = System.currentTimeMillis() - 3600000 * 3,
            isRead = true
        ),
        MessageEntity(
            id = "msg_2",
            bookingId = "book_1",
            senderId = "expert_1",
            senderRole = "EXPERT",
            text = "Namaste Ramesh ji. Based on the spindle-shaped lesions with ash-grey centers, this is Rice Blast (Magnaporthe oryzae). Do NOT apply more Nitrogen/Urea right now as excess Nitrogen promotes blast fungus spread.",
            timestamp = System.currentTimeMillis() - 3600000 * 2,
            isRead = true
        ),
        MessageEntity(
            id = "msg_3",
            bookingId = "book_1",
            senderId = "expert_1",
            senderRole = "EXPERT",
            text = "Recommended Treatment Plan:\n1. Spray Tricyclazole 75% WP @ 0.6g per litre of water (approx 120g/acre) OR Isoprothiolane 40% EC @ 1.5ml/litre.\n2. Ensure good water drainage in affected patches.\n3. Re-evaluate leaf progression in 4 days.",
            timestamp = System.currentTimeMillis() - 3600000 * 1,
            isRead = true
        )
    )

    val sampleReviews = listOf(
        ReviewEntity(
            id = "rev_1",
            bookingId = "book_past_1",
            expertId = "expert_1",
            farmerId = "farmer_1",
            farmerName = "Gurpreet Singh",
            rating = 5,
            reviewText = "Dr. Rahul diagnosed the yellow rust in my wheat crop very quickly. The recommended fungicide saved almost 80% of my yield. Highly recommended!",
            createdAt = System.currentTimeMillis() - 86400000 * 5
        ),
        ReviewEntity(
            id = "rev_2",
            bookingId = "book_past_2",
            expertId = "expert_1",
            farmerId = "farmer_2",
            farmerName = "Suresh Patel",
            rating = 5,
            reviewText = "Very patient and gave exact spray measurements in simple Hindi. Great platform for farmers.",
            createdAt = System.currentTimeMillis() - 86400000 * 9
        ),
        ReviewEntity(
            id = "rev_3",
            bookingId = "book_past_3",
            expertId = "expert_2",
            farmerId = "farmer_3",
            farmerName = "Vikas Yadav",
            rating = 5,
            reviewText = "Dr. Sunita gave me biological pest traps solution for bollworm that reduced chemical spray cost by 60%.",
            createdAt = System.currentTimeMillis() - 86400000 * 7
        )
    )

    val sampleNotifications = listOf(
        NotificationEntity(
            id = "notif_1",
            userId = "farmer_1",
            title = "Consultation Confirmed",
            message = "Your consultation with Dr. Rahul Sharma is scheduled for today at 10:00 AM.",
            type = "BOOKING",
            timestamp = System.currentTimeMillis() - 3600000 * 4,
            isRead = false
        ),
        NotificationEntity(
            id = "notif_2",
            userId = "farmer_1",
            title = "New Expert Message",
            message = "Dr. Rahul Sharma sent you a treatment plan for Rice Blast.",
            type = "MESSAGE",
            timestamp = System.currentTimeMillis() - 3600000 * 1,
            isRead = false
        ),
        NotificationEntity(
            id = "notif_3",
            userId = "expert_1",
            title = "New Consultation Booking",
            message = "Farmer Ramesh Kumar booked a Chat Consultation for Paddy problem. ₹269 credited upon completion.",
            type = "BOOKING",
            timestamp = System.currentTimeMillis() - 3600000 * 4,
            isRead = true
        )
    )
}
