package kr.paytogether.exchange

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("koreaexim_exchange")
data class Exchange(
    @Id
    val id: Long,
    val date: LocalDate,
    @Column("cur_unit")
    val currency: String,
    @Column("deal_bas_r")
    val exchangeRate: String,
)
