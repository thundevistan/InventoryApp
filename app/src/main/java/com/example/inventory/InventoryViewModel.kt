package com.example.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

/**
 * 1. ViewModel 은 DAO 를 통해 데이터베이스와 상호작용하여 UI에 데이터를 제공한다
 * 2. 모든 데이터베이스 작업은 기본 UI 스레드에서 벗어나 실행되어야 하며, 코루틴과 viewModelScope 를 사용하여 이를 수행한다
 * 3. 주 생성자로 DAO 객체를 받는다 -> ViewModelFactory 구현이 필요
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

	val allItems: LiveData<List<Item>> =
		itemDao.getItems().asLiveData()    // Flow -> LiveData 를 위해 asLiveData()

	// 직접적으로 DAO 의 insert 메소드 사용
	private fun insertItem(item: Item) {
		viewModelScope.launch {
			itemDao.insert(item)
		}
	}

	// 입력받은 문자열을 Item 객체로 변환하여 반환
	private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
		return Item(
			itemName = itemName,
			itemPrice = itemPrice.toDouble(),
			quantityInStock = itemCount.toInt()
		)
	}

	// Item 객체와 DAO 를 연결하는 부분
	fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
		val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
		insertItem(newItem)
	}

	// AddItemFragment 의 EditText 가 공백인지 판별 -> 프래그먼트가 아닌 ViewModel에서 실행해야 한다
	fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
		if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
			return false
		}
		return true
	}

	// id 에 해당하는 Item 객체를 반환
	fun retrieveItem(id: Int): LiveData<Item> {
		return itemDao.getItem(id).asLiveData()
	}

	// 직접적으로 DAO 의 update 메소드 사용
	private fun updateItem(item: Item) {
		viewModelScope.launch {
			itemDao.update(item)
		}
	}

	// 재고가 0 초과일 경우 (재고 -= 1) 씩 update
	fun sellItem(item: Item) {
		if (item.quantityInStock > 0) {
			val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
			updateItem(newItem)
		}
	}

	// 재고가 0 미만일 경우 false
	fun isStockAvailable(item: Item): Boolean {
		return (item.quantityInStock > 0)
	}

	// 직접적으로 DAO 의 delete 메소드 사용
	fun deleteItem(item: Item) {
		viewModelScope.launch {
			itemDao.delete(item)
		}
	}

	private fun getUpdatedItemEntry(
		itemId: Int,
		itemName: String,
		itemPrice: String,
		itemCount: String
	): Item {
		return Item(
			id = itemId,
			itemName = itemName,
			itemPrice = itemPrice.toDouble(),
			quantityInStock = itemCount.toInt()
		)
	}

	fun updateItem(
		itemId: Int,
		itemName: String,
		itemPrice: String,
		itemCount: String
	) {
		val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
		updateItem(updatedItem)
	}
}

// Tip: ViewModel 팩토리는 대부분 상용구 코드이므로 향후 ViewModel 팩토리에서 이 코드를 재사용할 수 있다
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		@Suppress("UNCHECKED_CAST")
		if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
			return InventoryViewModel(itemDao) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}