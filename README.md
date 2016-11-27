# FancyAdapters
A collection of customizable RecyclerView Adapters for Android, that provide various functionality like item selection, contextual action mode controls, drag&amp;drop and swiping, among other.

# Apps using FancyAdapters

<a target="_blank" href="https://play.google.com/store/apps/details?id=com.mobymagic.musicplayer">
<img src="https://lh3.googleusercontent.com/yaqTEg8aM8uR-IKM-Oz93PuQu4S2chRVOu01Opylu7zKvepIZUWIS4WHvObHPpdVIRY=w300-rw"
    title="Rx Music Player"
    alt="Rx Music Player" height="24">
</a> [**Rx Music Player**](https://play.google.com/store/apps/details?id=com.mobymagic.musicplayer), by Nnajiofor Onyinyechi


# Installation
In your project's `build.gradle` add jitpack's repository:
```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
And in your module's `build.gradle` add the following dependency:
```gradle
dependencies {
	...
	compile 'com.github.mikiloz92:fancyadapters:0.3.3'
}
```

# Library structure

![](https://github.com/MikiLoz92/FancyAdapters/blob/master/art/fancyadapters.png?raw=true "SelectableAdapter")

# Usage

Even though understanding and using the following adapters isn't really a straightforward process, I can guarantee you that the results are going to be worth it! So, without further due, let's take a look at the basics of the adapters:

## SelectableAdapter

**SelectableAdapter** is the simplest of them all. It just provides item selection functionality, combined with contextual action mode controls.

![alt text](https://github.com/MikiLoz92/FancyAdapters/blob/master/art/selectable_adapter.gif?raw=true "SelectableAdapter")

### Extending SelectableAdapter

**SelectableAdapter** is an abstract class, and that means you have to implement some of its functionalities on your own. Let's take a look at everything you need to implement and we'll explain step by step how to do it and what your code will be doing:

``` java
public class CustomAdapter extends SelectableAdapter<String, CustomAdapter.CustomViewHolder> {

    public CustomAdapter(List<String> items, RecyclerView recyclerView) {
        super(items, recyclerView);
    }

    @Override
    public CustomViewHolder onCreateSelectableViewHolder(ViewGroup parent, int viewType) { }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) { }

    @Override
    public void onItemSelected(CustomViewHolder holder, int position) { }

    @Override
    public void onItemDeselected(CustomViewHolder holder, int position) { }

    public class CustomActionModeCallback extends AdapterActionModeCallback {
    
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) { }
  
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { }
  
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) { }
        
        @Override
        public void onExitActionMode(ActionMode mode) { }
      
    }

    @Override
    public ActionMode startActionMode() { }

    @Override
    public void onSelectionUpdate(ActionMode mode, int selectedCount) { }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View itemView) {
            super(itemView);
        }
        
    }
    
}
```

First, notice that when extending from SelectableAdapter, you should provide a type for the elements your adapter will be storing, in this case ```String```s.

* `onCreateSelectableViewHolder`: This method creates a ViewHolder and returns it. It is called each time the RecyclerView needs a new ViewHolder, because either it doesn't have or it ran out of ViewHolders to recycle.
* `onBindViewHolder`: This method is used, generally, to replace the elements of a ViewHolder that was just recycled for the elements of a ViewHolder that is needed at the moment (this element corresponds to the `position` parameter). Furthermore, you should check if the ViewHolder that is being binded is selected or not (use the public method `isSelected(int position)`) and call `onItemSelected` or `onItemDeselected` accordingly.
* The `onItemSelected` and `onItemDeselected` methods should take care of activating or deactivating any view that provides visual feedback of the selection state of an item.
* We need to define an AdapterActionModeCallback child. This child contains the methods that will be called on every event of the ActionMode. Regarding the titles of the methods to override, this is pretty self-explanatory:
  * The `onCreateActionMode` is called whenever the intention of creating an ActionMode is made public. We should inflate any possible menu here, and perform any actions on the currently active (by active I mean "not recycled") ViewHolders, if needed. You can access them with the `viewHolders` public list. Watch the sample app to find out more about manipulating the ViewHolders programmatically.
  * The `onPrepareActionMode` method should set the title and perform any remaining actions once the ActionMode is created.
  * The `onActionItemClicked` method responds to action item click events.
  * The `onExitActionMode` is called when the ActionMode is being destroyed, be it because we clicked the back button or because we deselected the last item that remained selected. As with the `onCreateActionMode` method, any actions on the currently active ViewHolders should be performed here as well, if needed.
* The `startActionMode` method should create and return an ActionMode using an instance of the callback defined earlier. For the purpose of creating the ActionMode, you should employ any feasible strategy. For example, if you are extending the SelectableAdapter inside of an Activity class, this is easy: just call `startSupportActionMode()`. However, if you are doing it inside of a Fragment class, it gets a little trickier, you have to first access the parent Activity to call the method.
* The `onSelectionUpdate` is called when an item has just been selected or deselected. This method should update the ActionMode title to reflect the change in the number of selected items.
* Finally, a ViewHolder type needs to be provided to the SelectableAdapter. Within this ViewHolder's constructor, you should define any OnClickListener, OnLongClickListener or any input listener that triggers the selection mode, toggles, selects or deselects an item. For these four events, you should call `triggerSelectionMode()`, `toggleItem()`, `selectItem()` and `deselectItem()`, respectively.

## SuperSelectableAdapter

**SuperSelectableAdapter** adds drag&drop and swiping capabilities to **SelectableAdapter**.

![alt text](https://github.com/MikiLoz92/FancyAdapters/blob/master/art/super_selectable_adapter.gif?raw=true "SuperSelectableAdapter")

### Extending SuperSelectableAdapter

There are few more requisites to extend SuperSelectableAdapter than extending its parent class:

* Implement the `onMove` method: this method is called whenever a ViewHolder that you are currently dragging forces another ViewHolder to swap its position. You should perform any backend data movement here, or at least move the items from the `items` ArrayList of SuperSelectableAdapter.
* Implement the `onSwipe` method: this method is called whenever a ViewHolder is swiped. Any possible backend data removal or modification should be done here.
* As with SelectableAdapter, you can customize the ViewHolder behavior on input events by using the `triggerSelectionMode()`, `toggleItem()`, `selectItem()` and `deselectItem()` methods. Just one more thing left: you can call the `dragStart()` method whenver you want to start a drag. Generally, you would do this on your ViewHolder itemView's OnLongClickListener, but you could also add a *handle* View to your ViewHolder and start the drag when that *handle* is touched. The possibilities are endless!
* By default, an item will be selected when you start a drag on its ViewHolder and drop it on the same spot without moving it from there, as you can see on the gif image above. However, you can disable this behavior by using the `setTriggerSelectionModeByDroppingViewHolder(boolean triggerOnDrop)` method.

## SelectableViewAdapter

**SelectableViewAdapter** is a SuperSelectableAdapter that provides visual feedback of the selection status of an item. The adapter will perform a nice flip animation to replace the **selectableView** with the **selectedIndicatorView** (explained below) and viceversa. Any other visual feedback of the selection state that you want to provide you must do it on your own in the `onItemSelected`, `onItemDeselected` and `onBindViewHolder` methods, but keep in mind that if wou want to preserve the flip animations that come by default, you must invoke the parents' method with **`super`**  when overriding these methods (if you override `onBindViewHolder` then there's no need to even bother to define the `onBindSelectableViewHolder` method, you can just leave it blank).

![alt text](https://github.com/MikiLoz92/FancyAdapters/blob/master/art/selectable_view_adapter.gif?raw=true "SuperSelectableAdapter")

### Extending SelectableViewAdapter

What makes SelectableViewAdapter different from SuperSelectableAdapter is that you cannot provide a ViewHolder that extends from RecyclerView.ViewHolder. Instead, the ViewHolder must extend from **SelectableViewAdapter.ViewHolder**. This is because this particular subclass provides two abstract methods (`getSelectableViewID` and `getSelectedIndicatorResourceID`) that, when overriden, should provide, respectively:

1. The ID (R.id.\*) of the View that will act as a selectable View (in the gif below, it is the ID of the TextView that marks the row position (on the left of each item). This is considered the **selectableView**.
2. The layout ID (R.layout.\*) from which to inflate a View that will replace the View just described (in the gif below, it is a View that has tick mark). This is considered the **selectedIndicatorView**.

You can set whether the **selectableView** and **selectedIndicatorView** will respond to click events and toggle a selection with the method `setSelectableViewBehavior`. Just call it on your adapter instance. For example:
```java
mAdapter.setSelectableViewBehavior(SelectableViewBehavior.IGNORE_CLICK_EVENTS);
```
will tell the adapter to ignore click events on its **selectableView** and **selectedIndicatorView**. You can define another way to select an item, like long clicking it. The default behavior is `SelectableViewBehavior.RESPOND_TO_CLICK_EVENTS`, btw.

## HandleAdapter

**HandleAdapter** is basically the same as SelectableViewAdapter (extends from it too), but it already provides the code to drag an item by touching the  **selectableView** (the *handle* in this case). The idea here is that items here can **only** be dragged by touching the *handle*, so don't try to start a drag with a long click on the item; you could do that too, but better leave that option for presenting a menu dialog, for example... Just thoughts!

![alt text](https://github.com/MikiLoz92/FancyAdapters/blob/master/art/handle_adapter.gif?raw=true "SuperSelectableAdapter")

### Extending HandleAdapter

The process is the same as with SelectableViewAdapter, but the ViewHolder that you provide should not extend from SelectableViewAdapter.ViewHolder, but rather from **HandleAdapter.ViewHolder**. It's like this because this last class has already implemented the code for the *handle* view.
