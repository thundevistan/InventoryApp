package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 1. data 클래스의 이점: 컴파일러가 toString(), copy(), equals() 와 같은 유틸리티를 자동으로 생성
 * 2. Entity 클래스의 파라미터가 Entity fields 가 된다
 * 3. tableName 인수는 선택사항이지만 사용하는 것을 권장
 */
@Entity(tableName = "item")
data class Item (
	@PrimaryKey(autoGenerate = true)    // id 를 기본 키로 설정
	val id: Int = 0,
	@ColumnInfo(name = "name")          // Entity fields 를 변수명이 아닌 다른 이름으로 지정할 수도 있다
	val itemName: String,
	@ColumnInfo(name = "price")
	val itemPrice: Double,
	@ColumnInfo(name = "quantity")
	val quantityInStock: Int
)