package eis.domain.si;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import eis.domain.entities.Item;
import eis.domain.entities.StockItem;
import eis.domain.entities.ItemType;

public class StockItemFieldSetMapper implements FieldSetMapper<StockItem> {

	@Override
	public StockItem mapFieldSet(FieldSet fs) throws BindException {
		return new StockItem(new Item(mapItemType(fs.readString("itemType")), fs.readString("name"), fs.readString("number"), fs.readBigDecimal("price").doubleValue()), 
							fs.readInt("quantity"));
	}

	private ItemType mapItemType(String itemType) {
		if (itemType.equals(ItemType.FRAME.toString()))
			return ItemType.FRAME;
		else if (itemType.equals(ItemType.DRIVE.toString()))
			return ItemType.DRIVE;
		else if (itemType.equals(ItemType.WHEEL.toString()))
			return ItemType.WHEEL;
		else
			return ItemType.OTHER;
	}
	
}
