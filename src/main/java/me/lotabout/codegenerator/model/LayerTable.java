package me.lotabout.codegenerator.model;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.rits.cloning.Cloner;
import me.lotabout.codegenerator.CodeGeneratorSettings;
import me.lotabout.codegenerator.config.ProjectLayerConfig;
import me.lotabout.codegenerator.ui.LayerEditor;
import me.lotabout.codegenerator.util.GetProjectUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LayerTable extends JBTable {
	private static final Logger LOG = Logger.getInstance(LayerTable.class);
	private final MyTableModel myTableModel = new MyTableModel();
	private static final int NAME_COLUMN = 0;

	private static final int MODULE_COLUMN = 1;

	private static final int PATH_COLUMN = 2;

	public List<ProjectLayerConfig> myProjectLayerConfigs = new ArrayList<>();

	public LayerTable(List<ProjectLayerConfig> settingsLayers) {
		// 变成另一份
		Cloner cloner = new Cloner();
		myProjectLayerConfigs = cloner.deepClone(settingsLayers);
		setModel(myTableModel);
		TableColumn column = getColumnModel().getColumn(NAME_COLUMN);
		column.setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				final String macroValue = getLayerValueAt(row);
//				component.setForeground(macroValue.length() == 0
//					? new JBColor(original.getConflictsForegroundColor(), original.getConflictsForegroundColor())
//					: isSelected ? table.getSelectionForeground() : table.getForeground());
				return component;
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public String getLayerValueAt(int row) {
		return (String) getValueAt(row, PATH_COLUMN);
	}

	public void addLayer() {
		final LayerEditor macroEditor = new LayerEditor(new EditValidator(), null);
		System.out.println("hitt add");
		if (macroEditor.showAndGet()) {
			ProjectLayerConfig projectLayerConfig = macroEditor.getLayer();
			String name = projectLayerConfig.getName();
			myProjectLayerConfigs.add(projectLayerConfig);
			final int index = indexOfLayerWithName(name);
			LOG.assertTrue(index >= 0);
			myTableModel.fireTableDataChanged();
			setRowSelectionInterval(index, index);
		}
	}

	private boolean isValidRow(int selectedRow) {
		return selectedRow >= 0 && selectedRow < myProjectLayerConfigs.size();
	}

	public void moveUp() {
		int selectedRow = getSelectedRow();
		int index1 = selectedRow - 1;
		if (selectedRow != -1) {
			Collections.swap(myProjectLayerConfigs, selectedRow, index1);
		}
		setRowSelectionInterval(index1, index1);
	}

	public void moveDown() {
		int selectedRow = getSelectedRow();
		int index1 = selectedRow + 1;
		if (selectedRow != -1) {
			Collections.swap(myProjectLayerConfigs, selectedRow, index1);
		}
		setRowSelectionInterval(index1, index1);
	}


	public void removeSelectedLayeres() {
		final int[] selectedRows = getSelectedRows();
		if (selectedRows.length == 0) return;
		Arrays.sort(selectedRows);
		final int originalRow = selectedRows[0];
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			final int selectedRow = selectedRows[i];
			if (isValidRow(selectedRow)) {
				myProjectLayerConfigs.remove(selectedRow);
			}
		}
		myTableModel.fireTableDataChanged();
		if (originalRow < getRowCount()) {
			setRowSelectionInterval(originalRow, originalRow);
		} else if (getRowCount() > 0) {
			final int index = getRowCount() - 1;
			setRowSelectionInterval(index, index);
		}
	}

	public void commit() {
		Project project = GetProjectUtils.getProject();
		CodeGeneratorSettings settings = project.getService(CodeGeneratorSettings.class);
		settings.setProjectLayers(new ArrayList<>(myProjectLayerConfigs));
	}


	private int indexOfLayerWithName(String name) {
		for (int i = 0; i < myProjectLayerConfigs.size(); i++) {
			final ProjectLayerConfig pair = myProjectLayerConfigs.get(i);
			if (name.equals(pair.getName())) {
				return i;
			}
		}
		return -1;
	}

	public boolean editLayer() {
		if (getSelectedRowCount() != 1) {
			return false;
		}
		final int selectedRow = getSelectedRow();
		final ProjectLayerConfig projectLayerConfig = myProjectLayerConfigs.get(selectedRow);
		final LayerEditor editor = new LayerEditor(new EditValidator(), projectLayerConfig);
		if (editor.showAndGet()) {
			ProjectLayerConfig layer = editor.getLayer();
			projectLayerConfig.setName(layer.getName());
			projectLayerConfig.setModule(layer.getModule());
			projectLayerConfig.setPath(layer.getPath());
			myTableModel.fireTableDataChanged();
		}
		return true;
	}

	private class MyTableModel extends AbstractTableModel {
		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return myProjectLayerConfigs.size();
		}

		@Override
		public Class getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			final ProjectLayerConfig pair = myProjectLayerConfigs.get(rowIndex);
			switch (columnIndex) {
				case NAME_COLUMN:
					return pair.getName();
				case MODULE_COLUMN:
					return pair.getModule();
				case PATH_COLUMN:
					return pair.getPath();
			}
			LOG.error("Wrong indices");
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
				case NAME_COLUMN:
					return "From";
				case PATH_COLUMN:
					return "To";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

	public static class EditValidator implements Validator {

		@Override
		public boolean isOK(String name, String value) {
			return !name.isEmpty() && !value.isEmpty();
		}
	}

	public interface Validator {
		boolean isOK(String name, String value);
	}
}