package apprenticeshipScorecard

package object models {

  case class UKPRN(id: Long) extends AnyVal

  case class Learner(
                      satisfaction: Option[BigDecimal],
                      national_satisfaction: Option[BigDecimal],
                      age_under_19: Option[Int],
                      age_19_to_24: Option[Int],
                      age_25_plus: Option[Int],
                      total: Option[Int])

  case class Earnings(median: Option[BigDecimal], proportion_above_21k: Option[BigDecimal])

  case class QualificationStats(success_rate: Option[BigDecimal], retention_rate: Option[BigDecimal], achievement_rate: Option[BigDecimal])

  case class Address(
                      address1: Option[String],
                      address2: Option[String],
                      town: Option[String],
                      county: Option[String],
                      post_code: Option[String],
                      latitude: Option[BigDecimal],
                      longitude: Option[BigDecimal]
                    )

  case class Provider(
                       ukprn: UKPRN,
                       name: String,
                       provider_type: String,
                       region: String,
                       lea: String,
                       la: String,
                       address: Address,
                       website: Option[String])

  case class Apprenticeship(
                             subject_tier_2_code: String,
                             subject_tier_2_title: String,
                             learner: Learner,
                             description: String,
                             providerId: UKPRN,
                             stats: QualificationStats,
                             national_stats: QualificationStats,
                             earnings: Earnings,
                             national_earnings: Earnings,
                             average_cost: BigDecimal)

}
