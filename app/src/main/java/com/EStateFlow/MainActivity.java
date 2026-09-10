package com.EStateFlow;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity — migrated to use EstateRepository for all data & business logic.
 *
 * Ported functions from ES Old EstateViewModel / EstateRepository:
 *  - toggleFavorite          ✅ via repository
 *  - bookAppointment         ✅ showBookingDialog / confirmBooking
 *  - updateAppointmentStatus ✅ updateApptStatus
 *  - sendInquiry             ✅ showInquiryDialog / submitInquiry
 *  - replyToInquiry          ✅ sendChatMessage
 *  - markNotificationAsRead  ✅ markNotificationAsRead
 *  - markAllNotificationsAsRead ✅ markAllNotificationsAsRead
 *  - addProperty             ✅ showAddPropertyDialog / addCustomProperty
 *  - filteredProperties (price filter) ✅ fixed in filterExploreProperties
 *  - toggleUserRole          ✅ toggleRoleMode
 *  - navigateTo (all screens) ✅ showTab / bottom nav
 */
public class MainActivity extends AppCompatActivity {

    // ─── Repository (single source of truth) ──────────────────────────────────
    private EstateRepository repository;

    // ─── Header Views ──────────────────────────────────────────────────────────
    private TextView txtRoleBadge;
    private View btnToggleRole;
    private View btnNotifications;
    private View locationContainer;

    // ─── Tab Views ─────────────────────────────────────────────────────────────
    private View tabExploreView;
    private View tabSavedView;
    private View tabInboxView;
    private View tabProfileView;
    private View tabCalendarView;
    private View tabNotificationsView;
    private View tabAgentDashboardView;

    // ─── Explore Tab ───────────────────────────────────────────────────────────
    private EditText edtSearch;
    private ImageButton btnFilter;
    private RecyclerView recyclerCategories;
    private RecyclerView recyclerProperties;
    private TextView txtPropertyCount;
    private TextView txtViewAll;
    private TextView txtExploreEmpty;

    // ─── Saved Tab ─────────────────────────────────────────────────────────────
    private RecyclerView recyclerSavedProperties;
    private TextView txtSavedSubtitle;
    private TextView txtSavedEmpty;

    // ─── Inbox Tab ─────────────────────────────────────────────────────────────
    private RecyclerView recyclerConversations;

    // ─── Calendar Tab ──────────────────────────────────────────────────────────
    private RecyclerView recyclerAppointments;
    private TextView txtCalendarEmpty;

    // ─── Notifications Tab ────────────────────────────────────────────────────
    private RecyclerView recyclerNotifications;
    private TextView txtNotificationsEmpty;
    private TextView btnMarkAllRead;

    // ─── Profile Tab ───────────────────────────────────────────────────────────
    private SwitchMaterial switchAgentMode;
    private TextView txtProfileSavedCount;

    // ─── Agent Dashboard Tab ───────────────────────────────────────────────────
    private TextView txtDashListingCount;
    private TextView txtDashApptCount;
    private TextView txtDashInquiryCount;
    private MaterialButton btnAddProperty;

    // ─── Bottom Navigation ────────────────────────────────────────────────────
    private BottomNavigationView bottomNav;

    // ─── Adapters ─────────────────────────────────────────────────────────────
    private CategoryAdapter categoryAdapter;
    private PropertyAdapter exploreAdapter;
    private PropertyAdapter savedAdapter;
    private ConversationAdapter conversationAdapter;
    private AppointmentAdapter appointmentAdapter;
    private NotificationAdapter notificationAdapter;

    // ─── Lists (views onto repository data) ───────────────────────────────────
    private final List<Category> categoryList = new ArrayList<>();
    private final List<Property> exploreProperties = new ArrayList<>();
    private final List<Property> savedProperties = new ArrayList<>();

    // ─── Filter State ─────────────────────────────────────────────────────────
    private String currentCategory = "All";
    private String currentSearchQuery = "";
    private int selectedMinBeds = 0;
    private long filterMinPrice = 0L;
    private long filterMaxPrice = 5_000_000L;

    // ─── App State ────────────────────────────────────────────────────────────
    private boolean isAgentMode = false;

    // ─── Currently selected property (for booking/inquiry) ────────────────────
    private Property selectedProperty = null;
    private String bookingSelectedDate = "";
    private String bookingSelectedTime = "";

    // ─── Inquiry chat state ───────────────────────────────────────────────────
    private Inquiry selectedInquiry = null;

    // ═══════════════════════════════════════════════════════════════════════════
    // onCreate
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Init repository (single source of truth — ported from ES Old)
        repository = new EstateRepository();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, 0);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, insets.bottom);
            }
            return windowInsets;
        });

        initViews();
        setupCategories();
        setupRecyclerViews();
        setupListeners();
        refreshAllData();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // initViews
    // ═══════════════════════════════════════════════════════════════════════════

    private void initViews() {
        txtRoleBadge      = findViewById(R.id.txtRoleBadge);
        btnToggleRole     = findViewById(R.id.btnToggleRole);
        btnNotifications  = findViewById(R.id.btnNotifications);
        locationContainer = findViewById(R.id.locationContainer);

        tabExploreView         = findViewById(R.id.tabExploreView);
        tabSavedView           = findViewById(R.id.tabSavedView);
        tabInboxView           = findViewById(R.id.tabInboxView);
        tabProfileView         = findViewById(R.id.tabProfileView);
        tabCalendarView        = findViewById(R.id.tabCalendarView);
        tabNotificationsView   = findViewById(R.id.tabNotificationsView);
        tabAgentDashboardView  = findViewById(R.id.tabAgentDashboardView);

        edtSearch         = findViewById(R.id.edtSearch);
        btnFilter         = findViewById(R.id.btnFilter);
        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerProperties = findViewById(R.id.recyclerProperties);
        txtPropertyCount  = findViewById(R.id.txtPropertyCount);
        txtViewAll        = findViewById(R.id.txtViewAll);
        txtExploreEmpty   = findViewById(R.id.txtExploreEmpty);

        recyclerSavedProperties = findViewById(R.id.recyclerSavedProperties);
        txtSavedSubtitle        = findViewById(R.id.txtSavedSubtitle);
        txtSavedEmpty           = findViewById(R.id.txtSavedEmpty);

        recyclerConversations = findViewById(R.id.recyclerConversations);

        recyclerAppointments = findViewById(R.id.recyclerAppointments);
        txtCalendarEmpty     = findViewById(R.id.txtCalendarEmpty);

        recyclerNotifications   = findViewById(R.id.recyclerNotifications);
        txtNotificationsEmpty   = findViewById(R.id.txtNotificationsEmpty);
        btnMarkAllRead          = findViewById(R.id.btnMarkAllRead);

        switchAgentMode      = findViewById(R.id.switchAgentMode);
        txtProfileSavedCount = findViewById(R.id.txtProfileSavedCount);

        txtDashListingCount = findViewById(R.id.txtDashListingCount);
        txtDashApptCount    = findViewById(R.id.txtDashApptCount);
        txtDashInquiryCount = findViewById(R.id.txtDashInquiryCount);
        btnAddProperty      = findViewById(R.id.btnAddProperty);

        bottomNav = findViewById(R.id.bottomNav);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // setupCategories
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupCategories() {
        categoryList.add(new Category("1", "All",        R.drawable.ic_cat_all,       true));
        categoryList.add(new Category("2", "Houses",     R.drawable.ic_cat_house,     false));
        categoryList.add(new Category("3", "Apartments", R.drawable.ic_cat_apartment, false));
        categoryList.add(new Category("4", "Condos",     R.drawable.ic_cat_condo,     false));
        categoryList.add(new Category("5", "Townhouses", R.drawable.ic_cat_villa,     false));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // setupRecyclerViews
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupRecyclerViews() {
        // Category adapter
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            currentCategory = category.getName();
            filterExploreProperties();
        });
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCategories.setHasFixedSize(false);
        recyclerCategories.setAdapter(categoryAdapter);

        // Explore properties adapter
        exploreAdapter = new PropertyAdapter(exploreProperties, new PropertyAdapter.OnPropertyClickListener() {
            @Override
            public void onPropertyClick(Property property) {
                showPropertyDetailDialog(property);
            }
            @Override
            public void onFavoriteClick(Property property, int position) {
                repository.toggleFavorite(property.getId());
                updateSavedBadgeAndCount();
                String msg = property.isFavorite() ? "Removed from saved" : "Saved to your sanctuary";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerProperties.setLayoutManager(new LinearLayoutManager(this));
        recyclerProperties.setHasFixedSize(false);
        recyclerProperties.setItemViewCacheSize(10);
        recyclerProperties.setAdapter(exploreAdapter);

        // Saved adapter
        savedAdapter = new PropertyAdapter(savedProperties, new PropertyAdapter.OnPropertyClickListener() {
            @Override
            public void onPropertyClick(Property property) {
                showPropertyDetailDialog(property);
            }
            @Override
            public void onFavoriteClick(Property property, int position) {
                repository.toggleFavorite(property.getId());
                filterSavedProperties();
                exploreAdapter.updateList(exploreProperties);
                updateSavedBadgeAndCount();
            }
        });
        recyclerSavedProperties.setLayoutManager(new LinearLayoutManager(this));
        recyclerSavedProperties.setHasFixedSize(false);
        recyclerSavedProperties.setItemViewCacheSize(10);
        recyclerSavedProperties.setAdapter(savedAdapter);

        // Inbox / Conversations adapter (now uses Inquiry model)
        conversationAdapter = new ConversationAdapter(repository.getInquiries(), inquiry -> {
            inquiry.setUnread(false);
            conversationAdapter.notifyDataSetChanged();
            showChatDialog(inquiry);
        });
        recyclerConversations.setLayoutManager(new LinearLayoutManager(this));
        recyclerConversations.setHasFixedSize(false);
        recyclerConversations.setAdapter(conversationAdapter);

        // Appointments adapter (ported from ES Old CalendarAppointmentsScreen)
        appointmentAdapter = new AppointmentAdapter(repository.getAppointments(),
                new AppointmentAdapter.OnAppointmentClickListener() {
                    @Override
                    public void onStatusChange(ViewingAppointment appointment, AppointmentStatus newStatus) {
                        updateApptStatus(appointment.getId(), newStatus);
                    }
                    @Override
                    public void onClick(ViewingAppointment appointment) {
                        Toast.makeText(MainActivity.this,
                                appointment.getClientName() + " — " + appointment.getDate(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        recyclerAppointments.setLayoutManager(new LinearLayoutManager(this));
        recyclerAppointments.setHasFixedSize(false);
        recyclerAppointments.setAdapter(appointmentAdapter);

        // Notifications adapter (ported from ES Old NotificationsScreen)
        notificationAdapter = new NotificationAdapter(repository.getNotifications(), notif -> {
            markNotificationAsRead(notif.getId());
            // Navigate based on type (ported from ES Old onNotificationClick logic)
            String targetId = notif.getTargetId();
            if (targetId != null) {
                switch (notif.getType()) {
                    case VIEWING_BOOKED:
                        showTab(tabCalendarView);
                        bottomNav.setSelectedItemId(R.id.nav_calendar);
                        break;
                    case NEW_INQUIRY:
                        Inquiry matchedInq = repository.findInquiryById(targetId);
                        if (matchedInq != null) showChatDialog(matchedInq);
                        break;
                    case PRICE_DROP:
                    case LISTING_UPDATE:
                        showTab(tabExploreView);
                        bottomNav.setSelectedItemId(R.id.nav_explore);
                        break;
                }
            }
        });
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setHasFixedSize(false);
        recyclerNotifications.setAdapter(notificationAdapter);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // setupListeners
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupListeners() {
        // Search text watcher
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                filterExploreProperties();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter button
        btnFilter.setOnClickListener(v -> showFilterDialog());

        // Notifications bell — navigate to notifications tab
        btnNotifications.setOnClickListener(v -> {
            showTab(tabNotificationsView);
            bottomNav.setSelectedItemId(R.id.nav_notifications);
        });

        // Location dropdown
        locationContainer.setOnClickListener(v ->
                Toast.makeText(this, "Active Area: Malibu & Vancouver", Toast.LENGTH_SHORT).show());

        // Role toggle pill
        btnToggleRole.setOnClickListener(v -> toggleRoleMode());

        // Agent mode switch in Profile
        switchAgentMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != isAgentMode) toggleRoleMode();
        });

        // "View All"
        txtViewAll.setOnClickListener(v -> {
            currentCategory = "All";
            currentSearchQuery = "";
            selectedMinBeds = 0;
            edtSearch.setText("");
            for (int i = 0; i < categoryList.size(); i++) {
                categoryList.get(i).setSelected(i == 0);
            }
            categoryAdapter.notifyDataSetChanged();
            filterExploreProperties();
        });

        // Mark all notifications read
        btnMarkAllRead.setOnClickListener(v -> markAllNotificationsAsRead());

        // Agent dashboard quick-action buttons
        if (btnAddProperty != null)
            btnAddProperty.setOnClickListener(v -> showAddPropertyDialog());

        View btnDashCal = findViewById(R.id.btnDashViewCalendar);
        if (btnDashCal != null)
            btnDashCal.setOnClickListener(v -> {
                showTab(tabCalendarView);
                bottomNav.setSelectedItemId(R.id.nav_calendar);
            });

        View btnDashMsg = findViewById(R.id.btnDashViewMessages);
        if (btnDashMsg != null)
            btnDashMsg.setOnClickListener(v -> {
                showTab(tabInboxView);
                bottomNav.setSelectedItemId(R.id.nav_inbox);
            });

        View btnDashList = findViewById(R.id.btnDashViewListings);
        if (btnDashList != null)
            btnDashList.setOnClickListener(v -> {
                showTab(tabExploreView);
                bottomNav.setSelectedItemId(R.id.nav_explore);
            });

        // Bottom Navigation Tab Switching (ported from ES Old Screen enum navigation)
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                showTab(tabExploreView);
                return true;
            } else if (id == R.id.nav_search) {
                showTab(tabExploreView);
                edtSearch.requestFocus();
                return true;
            } else if (id == R.id.nav_saved) {
                filterSavedProperties();
                showTab(tabSavedView);
                return true;
            } else if (id == R.id.nav_inbox) {
                conversationAdapter.notifyDataSetChanged();
                showTab(tabInboxView);
                return true;
            } else if (id == R.id.nav_calendar) {
                refreshCalendarTab();
                showTab(tabCalendarView);
                return true;
            } else if (id == R.id.nav_notifications) {
                refreshNotificationsTab();
                showTab(tabNotificationsView);
                return true;
            } else if (id == R.id.nav_profile) {
                if (isAgentMode) {
                    refreshAgentDashboard();
                    showTab(tabAgentDashboardView);
                } else {
                    showTab(tabProfileView);
                }
                return true;
            }
            return false;
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // refreshAllData — initial data population
    // ═══════════════════════════════════════════════════════════════════════════

    private void refreshAllData() {
        filterExploreProperties();
        updateSavedBadgeAndCount();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showTab — ported from ES Old navigateTo(Screen)
    // ═══════════════════════════════════════════════════════════════════════════

    private void showTab(View targetTab) {
        tabExploreView.setVisibility(View.GONE);
        tabSavedView.setVisibility(View.GONE);
        tabInboxView.setVisibility(View.GONE);
        tabProfileView.setVisibility(View.GONE);
        tabCalendarView.setVisibility(View.GONE);
        tabNotificationsView.setVisibility(View.GONE);
        tabAgentDashboardView.setVisibility(View.GONE);
        targetTab.setVisibility(View.VISIBLE);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // toggleRoleMode — ported from ES Old toggleUserRole()
    // ═══════════════════════════════════════════════════════════════════════════

    private void toggleRoleMode() {
        isAgentMode = !isAgentMode;
        if (isAgentMode) {
            txtRoleBadge.setText("💼 Agent");
            switchAgentMode.setChecked(true);
            Toast.makeText(this, "Switched to Agent Mode", Toast.LENGTH_SHORT).show();
            refreshAgentDashboard();
            showTab(tabAgentDashboardView);
        } else {
            txtRoleBadge.setText("👤 Buyer");
            switchAgentMode.setChecked(false);
            Toast.makeText(this, "Switched to Buyer Mode", Toast.LENGTH_SHORT).show();
            showTab(tabExploreView);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // filterExploreProperties — ported from ES Old filteredProperties StateFlow
    //   Now also applies price filter (was broken in original ES New).
    // ═══════════════════════════════════════════════════════════════════════════

    private void filterExploreProperties() {
        exploreProperties.clear();

        for (Property p : repository.getProperties()) {
            boolean matchesCategory = currentCategory.equalsIgnoreCase("All") ||
                    p.getCategory().equalsIgnoreCase(currentCategory) ||
                    p.getTagText().toLowerCase().contains(currentCategory.toLowerCase());

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    p.getTitle().toLowerCase().contains(currentSearchQuery) ||
                    p.getLocation().toLowerCase().contains(currentSearchQuery) ||
                    p.getCityStateZip().toLowerCase().contains(currentSearchQuery) ||
                    p.getTagText().toLowerCase().contains(currentSearchQuery);

            boolean matchesBeds  = selectedMinBeds == 0 || p.getBeds() >= selectedMinBeds;
            // Price filter — ported from ES Old filteredProperties (was missing in original ES New)
            boolean matchesPrice = p.getPriceNumeric() >= filterMinPrice && p.getPriceNumeric() <= filterMaxPrice;

            if (matchesCategory && matchesSearch && matchesBeds && matchesPrice) {
                exploreProperties.add(p);
            }
        }

        exploreAdapter.updateList(exploreProperties);
        txtPropertyCount.setText(exploreProperties.size() + " Properties Available");

        if (exploreProperties.isEmpty()) {
            txtExploreEmpty.setVisibility(View.VISIBLE);
            recyclerProperties.setVisibility(View.GONE);
        } else {
            txtExploreEmpty.setVisibility(View.GONE);
            recyclerProperties.setVisibility(View.VISIBLE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // filterSavedProperties — ported from ES Old savedProperties StateFlow
    // ═══════════════════════════════════════════════════════════════════════════

    private void filterSavedProperties() {
        savedProperties.clear();
        savedProperties.addAll(repository.getSavedProperties());
        savedAdapter.updateList(savedProperties);

        if (txtSavedSubtitle != null)
            txtSavedSubtitle.setText(savedProperties.size() + " bookmarked homes in your sanctuary");
        if (txtProfileSavedCount != null)
            txtProfileSavedCount.setText(String.valueOf(savedProperties.size()));

        if (savedProperties.isEmpty()) {
            txtSavedEmpty.setVisibility(View.VISIBLE);
            recyclerSavedProperties.setVisibility(View.GONE);
        } else {
            txtSavedEmpty.setVisibility(View.GONE);
            recyclerSavedProperties.setVisibility(View.VISIBLE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // updateSavedBadgeAndCount
    // ═══════════════════════════════════════════════════════════════════════════

    private void updateSavedBadgeAndCount() {
        int count = repository.getSavedCount();
        if (txtProfileSavedCount != null)
            txtProfileSavedCount.setText(String.valueOf(count));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // refreshCalendarTab
    // ═══════════════════════════════════════════════════════════════════════════

    private void refreshCalendarTab() {
        appointmentAdapter.notifyListChanged();
        boolean empty = repository.getAppointments().isEmpty();
        if (txtCalendarEmpty != null)
            txtCalendarEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerAppointments.setVisibility(empty ? View.GONE : View.VISIBLE);

        // Update dashboard count
        if (txtDashApptCount != null)
            txtDashApptCount.setText(String.valueOf(repository.getAppointments().size()));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // refreshNotificationsTab
    // ═══════════════════════════════════════════════════════════════════════════

    private void refreshNotificationsTab() {
        notificationAdapter.notifyListChanged();
        boolean empty = repository.getNotifications().isEmpty();
        if (txtNotificationsEmpty != null)
            txtNotificationsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerNotifications.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // refreshAgentDashboard — ported from ES Old AgentDashboardScreen
    // ═══════════════════════════════════════════════════════════════════════════

    private void refreshAgentDashboard() {
        if (txtDashListingCount != null)
            txtDashListingCount.setText(String.valueOf(repository.getProperties().size()));
        if (txtDashApptCount != null)
            txtDashApptCount.setText(String.valueOf(repository.getAppointments().size()));
        if (txtDashInquiryCount != null)
            txtDashInquiryCount.setText(String.valueOf(repository.getInquiries().size()));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // markNotificationAsRead — ported from ES Old markNotificationAsRead()
    // ═══════════════════════════════════════════════════════════════════════════

    private void markNotificationAsRead(String id) {
        repository.markNotificationAsRead(id);
        notificationAdapter.notifyListChanged();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // markAllNotificationsAsRead — ported from ES Old markAllNotificationsAsRead()
    // ═══════════════════════════════════════════════════════════════════════════

    private void markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead();
        notificationAdapter.notifyListChanged();
        Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // updateApptStatus — ported from ES Old updateAppointmentStatus()
    // ═══════════════════════════════════════════════════════════════════════════

    private void updateApptStatus(String appointmentId, AppointmentStatus newStatus) {
        repository.updateAppointmentStatus(appointmentId, newStatus);
        appointmentAdapter.notifyListChanged();
        String label = newStatus == AppointmentStatus.COMPLETED ? "Appointment completed ✓" : "Appointment cancelled";
        Toast.makeText(this, label, Toast.LENGTH_SHORT).show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showFilterDialog — existing, extended with working price filter
    // ═══════════════════════════════════════════════════════════════════════════

    private void showFilterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter_properties);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView txtPriceRangeVal = dialog.findViewById(R.id.txtPriceRangeVal);
        RangeSlider sliderPrice   = dialog.findViewById(R.id.sliderPrice);
        ImageView btnCloseFilter  = dialog.findViewById(R.id.btnCloseFilter);
        MaterialButton btnReset   = dialog.findViewById(R.id.btnResetFilter);
        MaterialButton btnApply   = dialog.findViewById(R.id.btnApplyFilter);

        TextView chipAny = dialog.findViewById(R.id.chipBedAny);
        TextView chip1   = dialog.findViewById(R.id.chipBed1);
        TextView chip2   = dialog.findViewById(R.id.chipBed2);
        TextView chip3   = dialog.findViewById(R.id.chipBed3);
        TextView chip4   = dialog.findViewById(R.id.chipBed4);
        TextView[] bedChips = new TextView[]{chipAny, chip1, chip2, chip3, chip4};

        btnCloseFilter.setOnClickListener(v -> dialog.dismiss());

        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                int min = Math.round(values.get(0));
                int max = Math.round(values.get(1));
                txtPriceRangeVal.setText(String.format("$%,d - $%,d", min, max));
            }
        });

        for (int i = 0; i < bedChips.length; i++) {
            final int index = i;
            bedChips[i].setOnClickListener(v -> {
                selectedMinBeds = index;
                for (int j = 0; j < bedChips.length; j++) {
                    if (j == index) {
                        bedChips[j].setBackgroundResource(R.drawable.bg_chip_selected);
                        bedChips[j].setTextColor(Color.WHITE);
                    } else {
                        bedChips[j].setBackgroundResource(R.drawable.bg_chip_unselected);
                        bedChips[j].setTextColor(Color.DKGRAY);
                    }
                }
            });
        }

        btnReset.setOnClickListener(v -> {
            selectedMinBeds = 0;
            filterMinPrice  = 0L;
            filterMaxPrice  = 5_000_000L;
            sliderPrice.setValues(0f, 5000000f);
            txtPriceRangeVal.setText("$0 - $5,000,000");
            for (int j = 0; j < bedChips.length; j++) {
                if (j == 0) {
                    bedChips[j].setBackgroundResource(R.drawable.bg_chip_selected);
                    bedChips[j].setTextColor(Color.WHITE);
                } else {
                    bedChips[j].setBackgroundResource(R.drawable.bg_chip_unselected);
                    bedChips[j].setTextColor(Color.DKGRAY);
                }
            }
        });

        btnApply.setOnClickListener(v -> {
            // Apply price filter from slider — ported from ES Old setFilters()
            List<Float> values = sliderPrice.getValues();
            if (values.size() >= 2) {
                filterMinPrice = (long) Math.round(values.get(0));
                filterMaxPrice = (long) Math.round(values.get(1));
            }
            filterExploreProperties();
            dialog.dismiss();
            Toast.makeText(this, "Filters applied", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showPropertyDetailDialog — extended with Book Viewing + Send Inquiry buttons
    //   Ported from ES Old PropertyDetailsScreen
    // ═══════════════════════════════════════════════════════════════════════════

    private void showPropertyDetailDialog(Property property) {
        selectedProperty = property;

        // Pre-select first available date/time
        if (property.getAvailableDates() != null && !property.getAvailableDates().isEmpty())
            bookingSelectedDate = property.getAvailableDates().get(0);
        if (property.getAvailableTimeSlots() != null && !property.getAvailableTimeSlots().isEmpty())
            bookingSelectedTime = property.getAvailableTimeSlots().get(0);

        Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_property_details);

        ImageView imgHero    = dialog.findViewById(R.id.imgDetailHero);
        TextView txtPrice    = dialog.findViewById(R.id.txtDetailPrice);
        TextView txtAddress  = dialog.findViewById(R.id.txtDetailAddress);
        TextView txtBeds     = dialog.findViewById(R.id.txtDetailBeds);
        TextView txtBaths    = dialog.findViewById(R.id.txtDetailBaths);
        TextView txtSqft     = dialog.findViewById(R.id.txtDetailSqft);
        TextView txtType     = dialog.findViewById(R.id.txtDetailType);
        TextView txtDesc     = dialog.findViewById(R.id.txtDetailDescription);
        TextView txtStatus   = dialog.findViewById(R.id.txtDetailStatusTag);
        ImageView btnFavorite = dialog.findViewById(R.id.btnFavoriteDetail);
        View btnBack         = dialog.findViewById(R.id.btnBackFromDetail);
        MaterialButton btnBook    = dialog.findViewById(R.id.btnBookViewing);
        MaterialButton btnInquiry = dialog.findViewById(R.id.btnSendInquiry);

        imgHero.setImageResource(property.getImageResId());
        txtPrice.setText(property.getPrice());
        txtAddress.setText(property.getLocation());
        txtBeds.setText(property.getBeds() + " Beds");
        txtBaths.setText(property.getBaths() + " Baths");
        txtSqft.setText(String.format("%,d sqft", property.getSqft()));
        txtType.setText(property.getCategory());
        txtDesc.setText(property.getDescription());
        txtStatus.setText(property.getTagText());

        btnFavorite.setImageResource(property.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        btnFavorite.setOnClickListener(v -> {
            repository.toggleFavorite(property.getId());
            btnFavorite.setImageResource(property.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
            exploreAdapter.notifyDataSetChanged();
            updateSavedBadgeAndCount();
        });

        btnBack.setOnClickListener(v -> dialog.dismiss());

        // Book Viewing — ported from ES Old onBookAppointmentClick
        btnBook.setOnClickListener(v -> {
            dialog.dismiss();
            showBookingDialog(property);
        });

        // Send Inquiry — ported from ES Old onInquiryClick
        btnInquiry.setOnClickListener(v -> {
            dialog.dismiss();
            showInquiryDialog(property);
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showBookingDialog — ported from ES Old BookingDialog composable
    // ═══════════════════════════════════════════════════════════════════════════

    private void showBookingDialog(Property property) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_booking);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        TextView txtPropTitle    = dialog.findViewById(R.id.txtBookingPropertyTitle);
        LinearLayout layoutDates = dialog.findViewById(R.id.layoutDateChips);
        LinearLayout layoutTimes = dialog.findViewById(R.id.layoutTimeChips);
        TextInputEditText edtName  = dialog.findViewById(R.id.edtBookingName);
        TextInputEditText edtPhone = dialog.findViewById(R.id.edtBookingPhone);
        TextInputEditText edtEmail = dialog.findViewById(R.id.edtBookingEmail);
        TextInputEditText edtNotes = dialog.findViewById(R.id.edtBookingNotes);
        ImageView btnClose         = dialog.findViewById(R.id.btnCloseBooking);
        MaterialButton btnConfirm  = dialog.findViewById(R.id.btnConfirmBooking);

        txtPropTitle.setText(property.getTitle());

        // Build date chips
        for (String date : property.getAvailableDates()) {
            TextView chip = createChip(date, date.equals(bookingSelectedDate));
            chip.setOnClickListener(v -> {
                bookingSelectedDate = date;
                for (int i = 0; i < layoutDates.getChildCount(); i++) {
                    View child = layoutDates.getChildAt(i);
                    boolean selected = child == chip;
                    child.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
                    if (child instanceof TextView)
                        ((TextView) child).setTextColor(selected ? Color.WHITE : Color.DKGRAY);
                }
            });
            layoutDates.addView(chip);
        }

        // Build time chips
        for (String time : property.getAvailableTimeSlots()) {
            TextView chip = createChip(time, time.equals(bookingSelectedTime));
            chip.setOnClickListener(v -> {
                bookingSelectedTime = time;
                for (int i = 0; i < layoutTimes.getChildCount(); i++) {
                    View child = layoutTimes.getChildAt(i);
                    boolean selected = child == chip;
                    child.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
                    if (child instanceof TextView)
                        ((TextView) child).setTextColor(selected ? Color.WHITE : Color.DKGRAY);
                }
            });
            layoutTimes.addView(chip);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String name  = edtName.getText()  != null ? edtName.getText().toString()  : "";
            String phone = edtPhone.getText() != null ? edtPhone.getText().toString() : "";
            String email = edtEmail.getText() != null ? edtEmail.getText().toString() : "";
            String notes = edtNotes.getText() != null ? edtNotes.getText().toString() : "";

            dialog.dismiss();
            confirmBooking(property.getId(), name, phone, email, notes);
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // confirmBooking — ported from ES Old confirmBooking() in EstateViewModel
    // ═══════════════════════════════════════════════════════════════════════════

    private void confirmBooking(String propertyId, String name, String phone, String email, String notes) {
        ViewingAppointment appt = repository.bookAppointment(
                propertyId, bookingSelectedDate, bookingSelectedTime,
                name, phone, email, notes
        );
        showBookingSuccessDialog(appt);
        refreshCalendarTab();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showBookingSuccessDialog — ported from ES Old BookingSuccessDialog
    // ═══════════════════════════════════════════════════════════════════════════

    private void showBookingSuccessDialog(ViewingAppointment appt) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_booking_success);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        TextView txtSummary  = dialog.findViewById(R.id.txtBookingSuccessSummary);
        TextView txtProperty = dialog.findViewById(R.id.txtSuccessProperty);
        TextView txtDateTime = dialog.findViewById(R.id.txtSuccessDateTime);
        TextView txtClient   = dialog.findViewById(R.id.txtSuccessClient);
        MaterialButton btnDone = dialog.findViewById(R.id.btnSuccessDone);

        txtSummary.setText("Your appointment has been confirmed. You'll receive a reminder.");
        txtProperty.setText(appt.getPropertyTitle());
        txtDateTime.setText(appt.getDate() + " • " + appt.getTimeSlot());
        txtClient.setText(appt.getClientName());

        btnDone.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showInquiryDialog — ported from ES Old InquiryDialog composable
    // ═══════════════════════════════════════════════════════════════════════════

    private void showInquiryDialog(Property property) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_inquiry);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        TextView txtTitle        = dialog.findViewById(R.id.txtInquiryPropertyTitle);
        TextInputEditText edtMsg = dialog.findViewById(R.id.edtInquiryMessage);
        ImageView btnClose       = dialog.findViewById(R.id.btnCloseInquiry);
        MaterialButton btnSend   = dialog.findViewById(R.id.btnSendInquiry);

        txtTitle.setText(property.getTitle());

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String message = edtMsg.getText() != null ? edtMsg.getText().toString().trim() : "";
            if (message.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            submitInquiry(property.getId(), message);
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // submitInquiry — ported from ES Old submitInquiry() in EstateViewModel
    // ═══════════════════════════════════════════════════════════════════════════

    private void submitInquiry(String propertyId, String message) {
        Inquiry inq = repository.sendInquiry(propertyId, message);
        conversationAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Inquiry sent to " + (selectedProperty != null ? selectedProperty.getAgentName() : "Agent"), Toast.LENGTH_SHORT).show();
        // Navigate to inbox and open chat
        showTab(tabInboxView);
        bottomNav.setSelectedItemId(R.id.nav_inbox);
        showChatDialog(inq);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showChatDialog — ported from ES Old MessagesScreen chat view
    // ═══════════════════════════════════════════════════════════════════════════

    private void showChatDialog(Inquiry inquiry) {
        selectedInquiry = inquiry;

        Dialog dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_chat);

        TextView txtName      = dialog.findViewById(R.id.txtChatSenderName);
        TextView txtProp      = dialog.findViewById(R.id.txtChatPropertyTitle);
        RecyclerView recycler = dialog.findViewById(R.id.recyclerChatMessages);
        EditText edtMsg       = dialog.findViewById(R.id.edtChatMessage);
        ImageButton btnSend   = dialog.findViewById(R.id.btnSendChatMessage);
        View btnClose         = dialog.findViewById(R.id.btnCloseChat);

        txtName.setText(inquiry.getSenderName());
        txtProp.setText(inquiry.getPropertyTitle());

        List<ChatMessage> messages = new ArrayList<>(inquiry.getMessages());
        ChatMessageAdapter chatAdapter = new ChatMessageAdapter(messages);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(chatAdapter);
        recycler.scrollToPosition(messages.size() - 1);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // sendChatMessage — ported from ES Old sendChatMessage() in EstateViewModel
        btnSend.setOnClickListener(v -> {
            String text = edtMsg.getText().toString().trim();
            if (text.isEmpty()) return;
            edtMsg.setText("");

            // Determine role: if agent mode, send as agent, else as user/buyer
            boolean isAgent = isAgentMode;
            repository.replyToInquiry(inquiry.getId(), text, isAgent);
            messages.add(new ChatMessage(
                    java.util.UUID.randomUUID().toString(),
                    isAgent ? "agent" : "user", text, "Just now", isAgent
            ));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            recycler.scrollToPosition(messages.size() - 1);

            conversationAdapter.notifyDataSetChanged();

            // Auto-reply (ported from ES Old sendChatMessage coroutine delay logic)
            if (!isAgent) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String replyText;
                    String lower = text.toLowerCase();
                    if (lower.contains("strata") || lower.contains("prospectus")) {
                        replyText = "I'll have the strata documents and contingency reserve fund reports sent over to your email right away!";
                    } else if (lower.contains("tour") || lower.contains("walkthrough") || lower.contains("viewing") || lower.contains("available")) {
                        replyText = "The property is available! Feel free to pick a viewing time slot from the listing page or let me know your preferred time.";
                    } else if (lower.contains("price") || lower.contains("offer") || lower.contains("tax")) {
                        replyText = "Property taxes are approximately $4,850/yr. The sellers are open to reasonable offers this week.";
                    } else {
                        replyText = "Thanks for reaching out! I've noted your inquiry and will follow up with the full details shortly.";
                    }
                    repository.replyToInquiry(inquiry.getId(), replyText, true);
                    messages.add(new ChatMessage(
                            java.util.UUID.randomUUID().toString(),
                            "agent", replyText, "Just now", true
                    ));
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    recycler.scrollToPosition(messages.size() - 1);
                    conversationAdapter.notifyDataSetChanged();
                }, 1200);
            }
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // showAddPropertyDialog — ported from ES Old AddPropertyDialog composable
    // ═══════════════════════════════════════════════════════════════════════════

    private void showAddPropertyDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_property);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        ImageView btnClose             = dialog.findViewById(R.id.btnCloseAddProperty);
        TextInputEditText edtTitle     = dialog.findViewById(R.id.edtAddTitle);
        TextInputEditText edtAddress   = dialog.findViewById(R.id.edtAddAddress);
        TextInputEditText edtCSZ       = dialog.findViewById(R.id.edtAddCityStateZip);
        TextInputEditText edtPrice     = dialog.findViewById(R.id.edtAddPrice);
        com.google.android.material.switchmaterial.SwitchMaterial switchRental = dialog.findViewById(R.id.switchIsRental);
        TextInputEditText edtBeds      = dialog.findViewById(R.id.edtAddBeds);
        TextInputEditText edtBaths     = dialog.findViewById(R.id.edtAddBaths);
        TextInputEditText edtSqft      = dialog.findViewById(R.id.edtAddSqft);
        TextInputEditText edtType      = dialog.findViewById(R.id.edtAddType);
        TextInputEditText edtDesc      = dialog.findViewById(R.id.edtAddDescription);
        MaterialButton btnSubmit       = dialog.findViewById(R.id.btnSubmitAddProperty);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String title   = getText(edtTitle);
            String address = getText(edtAddress);
            String csz     = getText(edtCSZ);
            String priceStr = getText(edtPrice);
            String bedsStr = getText(edtBeds);
            String baths   = getText(edtBaths).isEmpty() ? "1" : getText(edtBaths);
            String sqftStr = getText(edtSqft);
            String type    = getText(edtType);
            String desc    = getText(edtDesc);
            boolean isRental = switchRental != null && switchRental.isChecked();

            if (title.isEmpty() || address.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in title, address, and price", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = 0;
            double beds  = 0;
            int sqft     = 0;
            try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
            try { beds  = Double.parseDouble(bedsStr);  } catch (NumberFormatException ignored) {}
            try { sqft  = Integer.parseInt(sqftStr);    } catch (NumberFormatException ignored) {}

            addCustomProperty(title, address, csz, price, isRental, beds, baths, sqft, type, desc, null);
            dialog.dismiss();
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // addCustomProperty — ported from ES Old addCustomProperty() in EstateViewModel
    // ═══════════════════════════════════════════════════════════════════════════

    private void addCustomProperty(String title, String address, String cityStateZip,
                                   double price, boolean isRental,
                                   double beds, String baths, int sqft,
                                   String propertyType, String description,
                                   List<String> amenities) {
        repository.addProperty(title, address, cityStateZip, price, isRental,
                beds, baths, sqft, propertyType, description, amenities);
        filterExploreProperties();
        refreshAgentDashboard();
        Toast.makeText(this, "\"" + title + "\" published!", Toast.LENGTH_SHORT).show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════════════════

    /** Creates a styled chip TextView for date/time pickers. */
    private TextView createChip(String label, boolean selected) {
        TextView chip = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 12, 0);
        chip.setLayoutParams(lp);
        chip.setText(label);
        chip.setTextSize(12f);
        chip.setPadding(28, 16, 28, 16);
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chip.setTextColor(selected ? Color.WHITE : Color.DKGRAY);
        chip.setClickable(true);
        chip.setFocusable(true);
        return chip;
    }

    /** Safely extracts trimmed text from a TextInputEditText. */
    private String getText(TextInputEditText edt) {
        return edt != null && edt.getText() != null ? edt.getText().toString().trim() : "";
    }
}
