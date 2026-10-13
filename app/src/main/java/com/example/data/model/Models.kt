package com.example.data.model

enum class UserRole {
    FARMER,
    EXPERT,
    ADMIN
}

enum class ProblemCategory(val displayName: String) {
    PLANT_PATHOLOGY("Plant Pathology (Diseases)"),
    ENTOMOLOGY("Entomology (Pest & Insects)"),
    SOIL_SCIENCE("Soil Science & Fertilizers"),
    AGRONOMY("Agronomy & Crop Management"),
    HORTICULTURE("Horticulture & Fruits/Vegetables"),
    WEED_MANAGEMENT("Weed Management"),
    IRRIGATION("Irrigation & Water Tech"),
    ORGANIC_FARMING("Organic Farming"),
    CROP_PROTECTION("General Crop Protection")
}

data class ExpertMatchResult(
    val expert: ExpertDetail,
    val matchScore: Int, // e.g. 96
    val matchReasons: List<String>
)

data class ExpertDetail(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val location: String,
    val avatarInitials: String,
    val title: String,
    val qualification: String,
    val institution: String,
    val experienceYears: Int,
    val bio: String,
    val pricePerConsultation: Double,
    val rating: Double,
    val reviewCount: Int,
    val consultationCount: Int,
    val isVerified: Boolean,
    val verificationDocName: String,
    val languagesSpoken: List<String>,
    val specializedCrops: List<String>,
    val expertiseCategories: List<String>
)

data class FeeCalculation(
    val consultationPrice: Double,
    val farmerFee: Double,
    val totalFarmerPaid: Double,
    val expertFee: Double,
    val expertEarnings: Double,
    val platformRevenue: Double
)
