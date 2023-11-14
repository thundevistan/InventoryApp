/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.InventoryApplication
import com.example.inventory.InventoryViewModel
import com.example.inventory.InventoryViewModelFactory
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

	private val navigationArgs: ItemDetailFragmentArgs by navArgs()

	private var _binding: FragmentAddItemBinding? = null
	private val binding get() = _binding!!

	private val viewModel: InventoryViewModel by activityViewModels {
		InventoryViewModelFactory((activity?.application as InventoryApplication).database.itemDao())
	}

	lateinit var item: Item

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentAddItemBinding.inflate(inflater, container, false)
		return binding.root
	}

	// ViewModel 의 isEntryValid() 함수를 구현
	private fun isEntryValid(): Boolean {
		return viewModel.isEntryValid(
			binding.itemName.text.toString(),
			binding.itemPrice.text.toString(),
			binding.itemCount.text.toString()
		)
	}

	// ViewModel 의 addNewItem() 함수를 구현
	private fun addNewItem() {
		if (isEntryValid()) {
			viewModel.addNewItem(
				binding.itemName.text.toString(),
				binding.itemPrice.text.toString(),
				binding.itemCount.text.toString()
			)
			val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
			findNavController().navigate(action)
		}
	}

	private fun updateItem() {
		if (isEntryValid()) {
			viewModel.updateItem(
				this.navigationArgs.itemId,
				this.binding.itemName.text.toString(),
				this.binding.itemPrice.text.toString(),
				this.binding.itemCount.text.toString()
			)
			val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
			findNavController().navigate(action)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// id 가 0 미만 == 새로 추가하는 데이터
		val id = navigationArgs.itemId
		if (id > 0) {
			viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
				item = selectedItem
				bind(item)
			}
		} else {
			binding.saveAction.setOnClickListener {
				addNewItem()
			}
		}
	}

	private fun bind(item: Item) = with(binding) {
		val price = "%.2f".format(item.itemPrice)

		itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
		itemPrice.setText(price, TextView.BufferType.SPANNABLE)
		itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)
		/**
		 * 1. Java 의 TextView 의 텍스트를 가져오는 getText() 함수는 TextView 내부의 "텍스트를 저장하는 버퍼에 대한 참조"를 가져오는 함수이다
		 * 2. 즉, getText() 함수는 내부 버퍼에 대한 참조를 CharSequence 타입으로 넘겨 받아 사용하는 것이다
		 * 3. 이 때, TextView의 "bufferType" 속성을 사용하면 리턴되는 타입을 CharSequence 외에 Spannable 또는 Editable로 변환(cast)하여 사용할 수 있다
		 *      1. normal (0) : CharSequence 를 리턴
		 *      2. spannable (1) : 오직 Spannable만 리턴
		 *      3. editable (2) : 오직 Spannable 또는 Editable만 리턴
		 *
		 * 4. TextView의 기본 값은 normal
		 * 5. EditText의 경우, 속성 설정과 관계없이 editable로 적용
		 * 6. CharSequence 의 서브 인터페이스 -> Spannable 의 서브 인터페이스 -> Editable
		 * 7. 각 인터페이스의 차이점은 다음과 같다
		 *      1. CharSequence : 바꿀 수 없는(immutable) 문자열(contents) 처리에 사용 (내용 불변)
		 *      2. Spannable : 문자열(contents)은 바꿀 수 없으나 markup 객체를 적용(attach) 또는 해제(detach) 가능
		 *      3. Editable : 문자열(contents) 및 markup 객체를 모두 변경 가능
		 *
		 * 8. BufferType 속성은 최소 버퍼 타입을 지정하는 것이므로, Editable 을 지정할 경우 셋 중 아무거나 타입 변환하여 사용할 수 있다
		 */

		saveAction.setOnClickListener { updateItem() }
	}

	override fun onDestroyView() {
		super.onDestroyView()
		// Hide keyboard.
		val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
				InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
		_binding = null
	}
}
