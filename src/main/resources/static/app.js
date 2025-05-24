import { apiGet, apiPost, apiPut, apiDelete } from './api.js';

// Initialize global userId
const userId = window.currentUserId || 1;

// --- Sidebar Navigation Handling ---
document.addEventListener('DOMContentLoaded', function() {
  console.log('DOM content loaded - setting up navigation');
  const navLinks = document.querySelectorAll('aside nav ul li a');
  const sections = document.querySelectorAll('main > section');

  function showSection(hash) {
    console.log('Showing section for hash:', hash);
    sections.forEach(section => {
      if ('#' + section.id === hash) {
        section.classList.remove('hidden');
        section.classList.add('active-section');
      } else {
        section.classList.add('hidden');
        section.classList.remove('active-section');
      }
    });
    navLinks.forEach(link => {
      if (link.getAttribute('href') === hash) {
        link.classList.add('text-blue-600', 'bg-blue-50', 'font-medium');
        link.classList.remove('text-gray-700', 'hover:bg-gray-100');
      } else {
        link.classList.remove('text-blue-600', 'bg-blue-50', 'font-medium');
        link.classList.add('text-gray-700', 'hover:bg-gray-100');
      }
    });
  }

  navLinks.forEach(link => {
    link.addEventListener('click', function(e) {
      e.preventDefault();
      const hash = this.getAttribute('href');
      console.log('Nav link clicked:', hash);
      showSection(hash);
      // Update the URL hash without scrolling
      history.pushState(null, null, hash);
    });
  });

  // On page load, show section based on hash or default to dashboard
  const initialHash = window.location.hash || '#dashboard';
  showSection(initialHash);

  // Handle browser back/forward navigation
  window.addEventListener('popstate', function() {
    const currentHash = window.location.hash || '#dashboard';
    showSection(currentHash);
  });

  // --- User ID (replace with actual login/session logic) ---

// --- Dashboard Integration ---
async function loadDashboard(userId) {
    try {
        console.log('Loading dashboard data for user ID:', userId);
        
        // Fetch user profile data
        const userData = await apiGet(`/users/${userId}`);
        document.getElementById('username').textContent = userData.name || 'Satvik';
        document.getElementById('user-balance').textContent = `₹${userData.totalBalance?.toLocaleString() || '100,000'}`;
        
        // Update welcome message
        const welcomeMessage = document.querySelector('.text-3xl.font-bold.text-white.mb-1');
        if (welcomeMessage) {
            console.log('Updating welcome message...');
            welcomeMessage.textContent = `Welcome back, ${userData.name || 'Satvik'}!`;
        }
        
        // Fetch balances
        const balanceTotal = document.querySelector('.dashboard-total-balance');
        const balanceSavings = document.querySelector('.dashboard-savings-balance');
        const balanceEmergency = document.querySelector('.dashboard-emergency-balance');
        
        if (balanceTotal) balanceTotal.textContent = `₹${userData.totalBalance?.toLocaleString() || '100,000'}`;
        if (balanceSavings) balanceSavings.textContent = `₹${userData.savingsBalance?.toLocaleString() || '40,000'}`;
        if (balanceEmergency) balanceEmergency.textContent = `₹${userData.emergencyFundBalance?.toLocaleString() || '20,000'}`;

        // Fetch recent transactions
        console.log('Fetching transactions from API...');
        const transactions = await apiGet(`/transactions/user/${userId}`);
        console.log('Transactions received:', transactions);
        renderRecentTransactions(transactions.slice(0, 5));

        // Fetch category-wise spending for insights
        console.log('Fetching category spending from API...');
        const categorySpending = await apiGet(`/transactions/user/${userId}/category-wise`);
        console.log('Category spending received:', categorySpending);
        renderSpendingInsights(categorySpending);
        
        // Also load data for all features to ensure everything is populated
        await Promise.all([
            renderReverseSpend(),
            renderGuiltSave(),
            renderEmergencyShield(),
            renderInvestmentGroups(),
            renderSubscriptions()
        ]);
        
        console.log('Dashboard data loaded successfully');
    } catch (err) {
        console.error('Failed to load dashboard:', err);
        // Don't show an alert as it's disruptive - just log to console
    }
}

function renderRecentTransactions(transactions) {
    const list = document.querySelector('.transactions-list');
    if (!list) return;
    list.innerHTML = '';
    transactions.forEach(tx => {
        const li = document.createElement('li');
        li.className = 'flex items-center bg-gray-50 rounded-lg px-4 py-3';
        li.innerHTML = `
            <div class="flex items-center gap-3 flex-1"><span class="text-2xl">💸</span> <span class="font-semibold">${tx.description || tx.category}</span> <span class="text-gray-400 text-sm ml-2">${new Date(tx.transactionDate).toLocaleString()}</span></div>
            <div class="flex items-center gap-2 transaction-amount-col"><span class="${tx.type === 'EXPENSE' ? 'text-red-600' : 'text-green-600'} font-bold">${tx.type === 'EXPENSE' ? '-' : '+'}₹${tx.amount}</span><span class="bg-blue-100 text-blue-700 rounded px-2 py-1 text-xs">${tx.category}</span></div>
        `;
        list.appendChild(li);
    });
}

function renderSpendingInsights(categorySpending) {
    Object.entries(categorySpending).forEach(([category, amount]) => {
        const el = document.querySelector(`.category-value[data-category="${category}"]`);
        if (el) el.textContent = `₹${amount}`;
    });
    renderDonutChartFromDOM();
}

// --- Reverse Spend ---
const reverseSpendForm = document.getElementById('reverse-spend-form');
const activeRestrictionsList = document.getElementById('active-restrictions-list');
const restrictionHistoryList = document.getElementById('restriction-history-list');
const rewardPercentage = document.getElementById('reward-percentage');
const rewardPercentageValue = document.getElementById('reward-percentage-value');
if (rewardPercentage && rewardPercentageValue) {
  rewardPercentage.addEventListener('input', function() {
    rewardPercentageValue.textContent = this.value + '%';
  });
}
async function renderReverseSpend() {
  console.log('Rendering Reverse Spend data');
  try {
    // Active challenges
    if (activeRestrictionsList) {
      activeRestrictionsList.innerHTML = '';
      const active = await apiGet(`/reverse-spend/user/${userId}/active`);
      
      if (active && active.length > 0) {
        active.forEach(challenge => {
          const remainingBudget = challenge.budgetLimit - (challenge.currentSpent || 0);
          const progressPercent = Math.min(100, ((challenge.currentSpent || 0) / challenge.budgetLimit) * 100);
          
          const div = document.createElement('div');
          div.className = 'restriction-card bg-white p-4 rounded-lg shadow-md mb-4';
          div.dataset.id = challenge.id;
          div.innerHTML = `
            <div class="flex justify-between items-center mb-2">
              <h4 class="text-lg font-semibold text-gray-800">${challenge.category}</h4>
              <span class="${challenge.isActive ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'} px-2 py-1 rounded text-xs font-bold">
                ${challenge.isActive ? 'Active' : 'Pending'}
              </span>
            </div>
            <p class="text-sm text-gray-600 mb-1">Budget Limit: <span class="font-medium">₹${challenge.budgetLimit?.toLocaleString() || 0}</span></p>
            <p class="text-sm text-gray-600 mb-1">Spent: <span class="font-medium text-${progressPercent > 80 ? 'red' : 'blue'}-600">₹${challenge.currentSpent?.toLocaleString() || 0}</span></p>
            <p class="text-sm text-gray-600 mb-1">Remaining: <span class="font-medium text-green-600">₹${remainingBudget?.toLocaleString() || 0}</span></p>
            <p class="text-sm text-gray-600 mb-3">Days Left: <span class="font-medium">${challenge.daysLeft || '-'}</span></p>
            
            <div class="w-full bg-gray-200 rounded-full h-2.5 mb-4">
              <div class="bg-${progressPercent > 80 ? 'red' : 'blue'}-600 h-2.5 rounded-full" style="width: ${progressPercent}%"></div>
            </div>
            
            <p class="text-xs text-gray-500 mb-3">Reward: ${challenge.rewardPercentage}% of unused budget</p>
            
            <div class="flex gap-2">
              <button class="btn secondary delete-restriction bg-red-100 text-red-700 px-3 py-1 rounded text-sm">Cancel Challenge</button>
              <button class="btn secondary log-spend bg-blue-100 text-blue-700 px-3 py-1 rounded text-sm">Log Spend</button>
            </div>
          `;
          activeRestrictionsList.appendChild(div);
        });
      } else {
        // Show placeholder or empty state
        activeRestrictionsList.innerHTML = `
          <div class="empty-state bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-500 mb-3">No active spending challenges</p>
            <p class="text-sm text-gray-400">Create a new challenge to start saving!</p>
          </div>
        `;
      }
    }
    
    // History
    if (restrictionHistoryList) {
      restrictionHistoryList.innerHTML = '';
      const completed = await apiGet(`/reverse-spend/user/${userId}/completed`);
      
      if (completed && completed.length > 0) {
        completed.forEach(h => {
          const isSuccess = h.status === 'success';
          const savedAmount = isSuccess ? (h.budgetLimit - h.currentSpent) : 0;
          const reward = isSuccess ? (savedAmount * (h.rewardPercentage / 100)) : 0;
          
          const div = document.createElement('div');
          div.className = 'history-item bg-white p-4 rounded-lg shadow-sm mb-3 border-l-4 ' + 
                        (isSuccess ? 'border-green-500' : 'border-red-500');
          div.innerHTML = `
            <div class="flex justify-between items-center mb-2">
              <h4 class="text-md font-semibold">${h.category}</h4>
              <span class="${isSuccess ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'} px-2 py-1 rounded text-xs font-bold">
                ${isSuccess ? 'Success' : 'Failed'}
              </span>
            </div>
            <div class="grid grid-cols-2 gap-2 text-sm">
              <p>Budget: <span class="font-medium">₹${h.budgetLimit?.toLocaleString() || 0}</span></p>
              <p>Spent: <span class="font-medium">₹${h.currentSpent?.toLocaleString() || 0}</span></p>
              ${isSuccess ? `<p>Saved: <span class="font-medium text-green-600">₹${savedAmount?.toLocaleString() || 0}</span></p>` : ''}
              ${isSuccess ? `<p>Reward: <span class="font-medium text-green-600">₹${reward?.toLocaleString() || 0}</span></p>` : ''}
            </div>
            <p class="text-xs text-gray-500 mt-2">Completed on ${new Date(h.endDate).toLocaleDateString()}</p>
          `;
          restrictionHistoryList.appendChild(div);
        });
      } else {
        // Show placeholder or empty state
        restrictionHistoryList.innerHTML = `
          <div class="empty-state bg-gray-50 p-4 rounded-lg text-center">
            <p class="text-gray-500">No challenge history yet</p>
          </div>
        `;
      }
    }
    console.log('Reverse Spend data rendered successfully');
  } catch (err) {
    console.error('Error rendering Reverse Spend data:', err);
  }
}
if (reverseSpendForm) {
  reverseSpendForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const category = document.getElementById('category').value;
    const budgetLimit = parseFloat(document.getElementById('budget-limit').value);
    const timePeriod = document.getElementById('time-period').value;
    const reward = parseFloat(document.getElementById('reward-percentage').value);
    const daysLeft = timePeriod === '3months' ? 90 : timePeriod === '6months' ? 180 : 365;
    await apiPost('/reverse-spend', {
      user: { id: userId },
      category,
      budgetLimit,
      rewardPercentage: reward,
      daysLeft
    });
    await renderReverseSpend();
    reverseSpendForm.reset();
    rewardPercentageValue.textContent = reward + '%';
    await loadDashboard(userId);
  });
}
if (activeRestrictionsList) {
  activeRestrictionsList.addEventListener('click', async function(e) {
    const card = e.target.closest('.restriction-card');
    const id = card.dataset.id;
    if (e.target.classList.contains('delete-restriction')) {
      await apiDelete(`/reverse-spend/${id}`);
      await renderReverseSpend();
      await loadDashboard(userId);
    }
    if (e.target.classList.contains('log-spend')) {
      const amount = parseFloat(prompt('Enter spend amount:'));
      if (!isNaN(amount) && amount > 0) {
        // Log spend as a transaction
        await apiPost('/transactions', {
          user: { id: userId },
          amount,
          type: 'EXPENSE',
          category: card.querySelector('h4').textContent,
          description: 'Reverse Spend Log',
          transactionDate: new Date().toISOString()
        });
        await apiPost(`/reverse-spend/user/${userId}/check-update`);
        await renderReverseSpend();
        await loadDashboard(userId);
      }
    }
  });
}
renderReverseSpend();

// --- Guilt Save ---
const guiltSaveForm = document.getElementById('guilt-save-form');
const activeGuiltRulesList = document.getElementById('active-guilt-rules-list');
const guiltSaveHistoryList = document.getElementById('guilt-save-history-list');
const guiltPercentage = document.getElementById('guilt-percentage');
const guiltPercentageValue = document.getElementById('guilt-percentage-value');
if (guiltPercentage && guiltPercentageValue) {
  guiltPercentage.addEventListener('input', function() {
    guiltPercentageValue.textContent = this.value + '%';
  });
}
async function renderGuiltSave() {
  console.log('Rendering Guilt Save data');
  try {
    // Active guilt save rules
    if (activeGuiltRulesList) {
      activeGuiltRulesList.innerHTML = '';
      const rules = await apiGet(`/guilt-save/user/${userId}/active`);
      
      if (rules && rules.length > 0) {
        rules.forEach(rule => {
          const div = document.createElement('div');
          div.className = 'rule-card bg-white p-4 rounded-lg shadow-md mb-4';
          div.dataset.id = rule.id;
          
          // Determine icon based on category
          let categoryIcon = '🛍️';
          if (rule.category.toLowerCase().includes('food')) categoryIcon = '🍔';
          if (rule.category.toLowerCase().includes('entertainment')) categoryIcon = '🎬';
          if (rule.category.toLowerCase().includes('shopping')) categoryIcon = '🛍️';
          if (rule.category.toLowerCase().includes('travel')) categoryIcon = '✈️';
          
          div.innerHTML = `
            <div class="flex justify-between items-center mb-3">
              <div class="flex items-center">
                <span class="text-2xl mr-2">${categoryIcon}</span>
                <h4 class="text-lg font-semibold text-gray-800">${rule.category}</h4>
              </div>
              <span class="bg-indigo-100 text-indigo-800 px-2 py-1 rounded text-xs font-bold">
                Active
              </span>
            </div>
            
            <div class="flex justify-between mb-3">
              <div>
                <p class="text-sm text-gray-600">Save <span class="font-bold text-indigo-600">${rule.savePercentage}%</span> to:</p>
                <p class="text-sm font-medium">${rule.destinationAccount}</p>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-600">Total saved:</p>
                <p class="text-lg font-bold text-green-600">₹${rule.savedAmount?.toLocaleString() || '0'}</p>
              </div>
            </div>
            
            <p class="text-xs text-gray-500 mb-3">Every time you spend in this category, ${rule.savePercentage}% will automatically be saved to your ${rule.destinationAccount.toLowerCase()}.</p>
            
            <div class="flex gap-2">
              <button class="btn secondary delete-rule bg-red-100 text-red-700 px-3 py-1 rounded text-sm">Remove Rule</button>
              <button class="btn secondary log-guilt bg-indigo-100 text-indigo-700 px-3 py-1 rounded text-sm">Log Spend</button>
            </div>
          `;
          activeGuiltRulesList.appendChild(div);
        });
      } else {
        // Show placeholder or empty state
        activeGuiltRulesList.innerHTML = `
          <div class="empty-state bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-500 mb-3">No active guilt save rules</p>
            <p class="text-sm text-gray-400">Create a rule to start saving while you spend!</p>
          </div>
        `;
      }
    }
    
    // Guilt save history/transactions
    if (guiltSaveHistoryList) {
      guiltSaveHistoryList.innerHTML = '';
      
      try {
        // Try to fetch guilt save transactions if endpoint exists
        const history = await apiGet(`/guilt-save/user/${userId}/history`);
        
        if (history && history.length > 0) {
          history.forEach(item => {
            const div = document.createElement('div');
            div.className = 'history-item bg-white p-3 rounded-lg shadow-sm mb-2 border-l-4 border-indigo-300';
            div.innerHTML = `
              <div class="flex justify-between items-center">
                <div>
                  <h4 class="font-medium">${item.category}</h4>
                  <p class="text-xs text-gray-500">${new Date(item.createdAt).toLocaleDateString()}</p>
                </div>
                <div class="text-right">
                  <p class="text-sm">Original spend: <span class="font-medium">₹${item.originalAmount?.toLocaleString() || '0'}</span></p>
                  <p class="text-sm">Saved: <span class="font-medium text-indigo-600">₹${item.savedAmount?.toLocaleString() || '0'}</span></p>
                </div>
              </div>
            `;
            guiltSaveHistoryList.appendChild(div);
          });
        } else {
          createSampleGuiltHistory();
        }
      } catch (err) {
        // If endpoint not implemented, show sample data
        console.log('Guilt save history endpoint not available, showing sample data');
        createSampleGuiltHistory();
      }
    }
    
    function createSampleGuiltHistory() {
      // Create sample history items if real data is not available
      const sampleHistory = [
        { category: 'Food & Dining', originalAmount: 2500, savedAmount: 250, createdAt: new Date('2025-05-20') },
        { category: 'Shopping', originalAmount: 7500, savedAmount: 750, createdAt: new Date('2025-05-15') },
        { category: 'Entertainment', originalAmount: 1200, savedAmount: 90, createdAt: new Date('2025-05-10') },
        { category: 'Food & Dining', originalAmount: 1800, savedAmount: 180, createdAt: new Date('2025-05-05') }
      ];
      
      sampleHistory.forEach(item => {
        const div = document.createElement('div');
        div.className = 'history-item bg-white p-3 rounded-lg shadow-sm mb-2 border-l-4 border-indigo-300';
        div.innerHTML = `
          <div class="flex justify-between items-center">
            <div>
              <h4 class="font-medium">${item.category}</h4>
              <p class="text-xs text-gray-500">${item.createdAt.toLocaleDateString()}</p>
            </div>
            <div class="text-right">
              <p class="text-sm">Original spend: <span class="font-medium">₹${item.originalAmount?.toLocaleString() || '0'}</span></p>
              <p class="text-sm">Saved: <span class="font-medium text-indigo-600">₹${item.savedAmount?.toLocaleString() || '0'}</span></p>
            </div>
          </div>
        `;
        guiltSaveHistoryList.appendChild(div);
      });
    }
    
    console.log('Guilt Save data rendered successfully');
  } catch (err) {
    console.error('Error rendering Guilt Save data:', err);
  }
}
if (guiltSaveForm) {
  guiltSaveForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const category = document.getElementById('guilt-category').value;
    const savePercentage = parseFloat(document.getElementById('guilt-percentage').value);
    const destinationAccount = document.getElementById('guilt-destination').value;
    await apiPost('/guilt-save', {
      user: { id: userId },
      category,
      savePercentage,
      destinationAccount
    });
    await renderGuiltSave();
    guiltSaveForm.reset();
    guiltPercentageValue.textContent = savePercentage + '%';
    await loadDashboard(userId);
  });
}
if (activeGuiltRulesList) {
  activeGuiltRulesList.addEventListener('click', async function(e) {
    const card = e.target.closest('.rule-card');
    const id = card.dataset.id;
    if (e.target.classList.contains('delete-rule')) {
      await apiDelete(`/guilt-save/${id}`);
      await renderGuiltSave();
      await loadDashboard(userId);
    }
    if (e.target.classList.contains('log-guilt')) {
      const amount = parseFloat(prompt('Enter spend amount:'));
      if (!isNaN(amount) && amount > 0) {
        await apiPost(`/guilt-save/user/${userId}/process?category=${encodeURIComponent(card.querySelector('h4').textContent)}&amount=${amount}`);
        await renderGuiltSave();
        await loadDashboard(userId);
      }
    }
  });
}
renderGuiltSave();

// --- Emergency Shield ---
const emergencyShieldForm = document.getElementById('emergency-shield-form');
const activeShieldsList = document.getElementById('active-shields-list');
const withdrawalRequestsList = document.getElementById('withdrawal-requests-list');
async function renderEmergencyShield() {
  console.log('Rendering Emergency Shield data');
  try {
    // Active shields
    if (activeShieldsList) {
      activeShieldsList.innerHTML = '';
      const shields = await apiGet(`/emergency-shield/user/${userId}/active`);
      
      if (shields && shields.length > 0) {
        shields.forEach(shield => {
          const div = document.createElement('div');
          div.className = 'shield-card bg-white p-4 rounded-lg shadow-md mb-4';
          div.dataset.id = shield.id;
          
          div.innerHTML = `
            <div class="flex justify-between items-center mb-3">
              <div class="flex items-center">
                <span class="text-2xl mr-2">🔐</span>
                <h4 class="text-lg font-semibold text-gray-800">${shield.accountName || 'Emergency Fund'}</h4>
              </div>
              <span class="bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs font-bold">
                Protected
              </span>
            </div>
            
            <div class="bg-blue-50 p-3 rounded-lg mb-3">
              <div class="flex items-center mb-2">
                <span class="text-xl mr-2">👤</span>
                <div>
                  <p class="text-sm font-medium">Guardian: ${shield.friendName}</p>
                  <p class="text-xs text-gray-500">${shield.friendEmail || ''}</p>
                  <p class="text-xs text-gray-500">${shield.friendPhone || ''}</p>
                </div>
              </div>
              <p class="text-xs text-gray-600">Your trusted friend must approve any withdrawals from this account</p>
            </div>
            
            <div class="flex justify-between mb-3">
              <div>
                <p class="text-sm text-gray-600">Protected Amount:</p>
                <p class="text-lg font-bold text-blue-600">₹${shield.shieldedAmount?.toLocaleString() || '0'}</p>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-600">Withdrawal Threshold:</p>
                <p class="text-lg font-bold">₹${shield.threshold?.toLocaleString() || '0'}</p>
              </div>
            </div>
            
            <p class="text-xs text-gray-500 mb-3">Any withdrawal above ₹${shield.threshold?.toLocaleString() || '0'} requires approval from ${shield.friendName}.</p>
            
            <div class="flex gap-2">
              <button class="btn secondary delete-shield bg-red-100 text-red-700 px-3 py-1 rounded text-sm">Remove Shield</button>
              <button class="btn secondary request-withdrawal bg-blue-100 text-blue-700 px-3 py-1 rounded text-sm">Request Withdrawal</button>
            </div>
          `;
          activeShieldsList.appendChild(div);
        });
      } else {
        // Show placeholder or empty state
        activeShieldsList.innerHTML = `
          <div class="empty-state bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-500 mb-3">No active emergency shields</p>
            <p class="text-sm text-gray-400">Protect your funds by adding a trusted guardian!</p>
          </div>
        `;
      }
    }
    
    // Withdrawal requests
    if (withdrawalRequestsList) {
      withdrawalRequestsList.innerHTML = '';
      
      try {
        // Try to fetch withdrawal requests if endpoint exists
        const requests = await apiGet(`/emergency-shield/user/${userId}/requests`);
        
        if (requests && requests.length > 0) {
          requests.forEach(req => {
            const div = document.createElement('div');
            div.className = `history-item bg-white p-3 rounded-lg shadow-sm mb-2 border-l-4 
                         ${req.status === 'approved' ? 'border-green-500' : 
                           req.status === 'rejected' ? 'border-red-500' : 'border-yellow-500'}`;
            div.innerHTML = `
              <div class="flex justify-between items-center">
                <div>
                  <h4 class="font-medium">Withdrawal Request</h4>
                  <p class="text-xs text-gray-500">${new Date(req.requestDate).toLocaleDateString()}</p>
                </div>
                <div class="text-right">
                  <p class="text-sm">Amount: <span class="font-medium">₹${req.amount?.toLocaleString() || '0'}</span></p>
                  <p class="text-sm">Status: 
                    <span class="font-medium 
                      ${req.status === 'approved' ? 'text-green-600' : 
                        req.status === 'rejected' ? 'text-red-600' : 'text-yellow-600'}">
                      ${req.status.charAt(0).toUpperCase() + req.status.slice(1)}
                    </span>
                  </p>
                </div>
              </div>
              <p class="text-sm mt-2">Reason: ${req.reason}</p>
              ${req.responseComment ? `<p class="text-sm mt-1">Response: ${req.responseComment}</p>` : ''}
            `;
            withdrawalRequestsList.appendChild(div);
          });
        } else {
          createSampleWithdrawalRequests();
        }
      } catch (err) {
        // If endpoint not implemented, show sample data
        console.log('Withdrawal requests endpoint not available, showing sample data');
        createSampleWithdrawalRequests();
      }
    }
    
    function createSampleWithdrawalRequests() {
      // Create sample withdrawal requests if real data is not available
      const sampleRequests = [
        { 
          requestDate: new Date('2025-05-15'), 
          amount: 6000, 
          reason: 'Medical emergency', 
          status: 'approved',
          responseComment: 'Approved for medical needs. Take care!'
        },
        { 
          requestDate: new Date('2025-05-05'), 
          amount: 4000, 
          reason: 'Car repair', 
          status: 'approved',
          responseComment: 'Necessary expense, approved.'
        },
        { 
          requestDate: new Date('2025-04-28'), 
          amount: 8000, 
          reason: 'Weekend trip', 
          status: 'rejected',
          responseComment: 'Not an emergency. Save for this separately.'
        },
        { 
          requestDate: new Date('2025-04-20'), 
          amount: 3000, 
          reason: 'New smartphone', 
          status: 'rejected',
          responseComment: 'This can wait until next month.'
        }
      ];
      
      sampleRequests.forEach(req => {
        const div = document.createElement('div');
        div.className = `history-item bg-white p-3 rounded-lg shadow-sm mb-2 border-l-4 
                     ${req.status === 'approved' ? 'border-green-500' : 
                       req.status === 'rejected' ? 'border-red-500' : 'border-yellow-500'}`;
        div.innerHTML = `
          <div class="flex justify-between items-center">
            <div>
              <h4 class="font-medium">Withdrawal Request</h4>
              <p class="text-xs text-gray-500">${req.requestDate.toLocaleDateString()}</p>
            </div>
            <div class="text-right">
              <p class="text-sm">Amount: <span class="font-medium">₹${req.amount?.toLocaleString() || '0'}</span></p>
              <p class="text-sm">Status: 
                <span class="font-medium 
                  ${req.status === 'approved' ? 'text-green-600' : 
                    req.status === 'rejected' ? 'text-red-600' : 'text-yellow-600'}">
                  ${req.status.charAt(0).toUpperCase() + req.status.slice(1)}
                </span>
              </p>
            </div>
          </div>
          <p class="text-sm mt-2">Reason: ${req.reason}</p>
          ${req.responseComment ? `<p class="text-sm mt-1">Response: ${req.responseComment}</p>` : ''}
        `;
        withdrawalRequestsList.appendChild(div);
      });
    }
    
    console.log('Emergency Shield data rendered successfully');
  } catch (err) {
    console.error('Error rendering Emergency Shield data:', err);
  }
}
if (emergencyShieldForm) {
  emergencyShieldForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const friendName = document.getElementById('trusted-friend-name').value;
    const friendEmail = document.getElementById('trusted-friend-email').value;
    const friendPhone = document.getElementById('trusted-friend-phone').value;
    const accountName = document.getElementById('protected-account').value;
    const threshold = parseFloat(document.getElementById('withdrawal-threshold').value);
    await apiPost('/emergency-shield', {
      user: { id: userId },
      accountName,
      friendName,
      friendEmail,
      friendPhone,
      threshold
    });
    await renderEmergencyShield();
    emergencyShieldForm.reset();
    await loadDashboard(userId);
  });
}
if (activeShieldsList) {
  activeShieldsList.addEventListener('click', async function(e) {
    const card = e.target.closest('.shield-card');
    const id = card.dataset.id;
    if (e.target.classList.contains('delete-shield')) {
      await apiDelete(`/emergency-shield/${id}`);
      await renderEmergencyShield();
      await loadDashboard(userId);
    }
    if (e.target.classList.contains('request-withdrawal')) {
      const amount = parseFloat(prompt('Enter withdrawal amount:'));
      const reason = prompt('Reason for withdrawal:');
      if (!isNaN(amount) && amount > 0 && reason) {
        // Optionally, implement withdrawal request API
        alert('Withdrawal request submitted (backend integration needed)');
      }
    }
  });
}
renderEmergencyShield();

// --- Group Investment Pot ---
const createGroupBtn = document.getElementById('create-group-btn');
const createGroupModal = document.getElementById('create-group-modal');
const closeModal = document.querySelector('.close-modal');
const createGroupForm = document.getElementById('create-group-form');
const investmentGroupsList = document.getElementById('investment-groups-list');
async function renderInvestmentGroups() {
  console.log('Rendering Group Investment data');
  try {
    // Group investment pots/groups
    if (investmentGroupsList) {
      investmentGroupsList.innerHTML = '';
      const groups = await apiGet(`/group-investment/user/${userId}`);
      
      if (groups && groups.length > 0) {
        groups.forEach(group => {
          // Calculate progress toward goal if available
          let progressPercent = 50; // Default
          if (group.goal && group.goal.includes('₹')) {
            const goalAmount = parseFloat(group.goal.replace(/[^\d]/g, ''));
            if (goalAmount > 0 && group.totalAmount) {
              progressPercent = Math.min(100, (group.totalAmount / goalAmount) * 100);
            }
          }
          
          // Determine investment type icon
          let investmentIcon = '💰';
          if (group.investmentType.toLowerCase().includes('equity')) investmentIcon = '📈';
          if (group.investmentType.toLowerCase().includes('fixed')) investmentIcon = '🏦';
          if (group.investmentType.toLowerCase().includes('mutual')) investmentIcon = '💱';
          
          // Create the card
          const div = document.createElement('div');
          div.className = 'investment-group-card bg-white p-4 rounded-lg shadow-md mb-6';
          div.dataset.id = group.id;
          
          let membersHtml = '';
          let totalContributed = 0;
          const members = group.members || [];
          
          members.forEach(m => {
            const memberAmount = parseFloat(m.amount || 0);
            totalContributed += memberAmount;
            membersHtml += `
              <li class="flex justify-between items-center py-1 border-b border-gray-100">
                <span class="flex items-center gap-1">
                  <span class="text-sm">👤</span>
                  <span>${m.name || m.email || 'Member ' + m.id}</span>
                </span>
                <span class="text-blue-600 font-medium">₹${memberAmount.toLocaleString() || 0}</span>
              </li>
            `;
          });
          
          // Calculate projected returns (simple 10% annual return)
          const projectedReturns = group.totalAmount * 0.1;
          const monthlyGrowth = projectedReturns / 12;
          
          div.innerHTML = `
            <div class="flex justify-between items-center mb-3">
              <div class="flex items-center">
                <span class="text-2xl mr-2">${investmentIcon}</span>
                <h4 class="text-lg font-semibold text-gray-800">${group.name}</h4>
              </div>
              <span class="bg-purple-100 text-purple-800 px-2 py-1 rounded text-xs font-bold">
                ${members.length} Members
              </span>
            </div>
            
            <p class="text-sm text-gray-600 mb-3">${group.goal}</p>
            
            <div class="progress-container mb-4">
              <div class="flex justify-between text-sm mb-1">
                <span>Progress</span>
                <span>${progressPercent.toFixed(1)}%</span>
              </div>
              <div class="w-full bg-gray-200 rounded-full h-2.5">
                <div class="bg-purple-600 h-2.5 rounded-full" style="width: ${progressPercent}%"></div>
              </div>
            </div>
            
            <div class="grid grid-cols-2 gap-4 mb-4">
              <div class="bg-purple-50 p-3 rounded-lg">
                <p class="text-xs text-gray-500">Monthly Contribution</p>
                <p class="text-lg font-bold text-purple-700">₹${group.monthlyContribution?.toLocaleString() || 0}</p>
              </div>
              <div class="bg-green-50 p-3 rounded-lg">
                <p class="text-xs text-gray-500">Total Invested</p>
                <p class="text-lg font-bold text-green-700">₹${group.totalAmount?.toLocaleString() || 0}</p>
              </div>
            </div>
            
            <div class="mb-3">
              <p class="font-medium mb-1">Investment Details</p>
              <div class="bg-gray-50 p-2 rounded-lg">
                <p class="text-sm">Type: <span class="font-medium">${group.investmentType}</span></p>
                <p class="text-sm">Est. Monthly Growth: <span class="font-medium text-green-600">₹${monthlyGrowth.toLocaleString(undefined, {maximumFractionDigits: 0})}</span></p>
                <p class="text-sm">Projected Annual Return: <span class="font-medium text-green-600">₹${projectedReturns.toLocaleString(undefined, {maximumFractionDigits: 0})}</span></p>
              </div>
            </div>
            
            <div class="mb-4">
              <p class="font-medium mb-1">Members & Contributions</p>
              <ul class="bg-gray-50 rounded-lg p-2">
                ${membersHtml}
              </ul>
            </div>
            
            <div class="flex gap-2">
              <button class="btn secondary add-contribution bg-purple-100 text-purple-700 px-3 py-1 rounded text-sm">Add Contribution</button>
              <button class="btn secondary invite-member bg-blue-100 text-blue-700 px-3 py-1 rounded text-sm">Invite Member</button>
            </div>
          `;
          investmentGroupsList.appendChild(div);
        });
      } else {
        // Show placeholder or empty state
        investmentGroupsList.innerHTML = `
          <div class="empty-state bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-500 mb-3">No investment groups yet</p>
            <p class="text-sm text-gray-400">Create a group pot to start investing collectively!</p>
          </div>
        `;
      }
    }
    
    console.log('Group Investment data rendered successfully');
  } catch (err) {
    console.error('Error rendering Group Investment data:', err);
  }
}
if (createGroupBtn && createGroupModal && closeModal && createGroupForm) {
  createGroupBtn.addEventListener('click', function() {
    createGroupModal.style.display = 'flex';
  });
  closeModal.addEventListener('click', function() {
    createGroupModal.style.display = 'none';
  });
  window.addEventListener('click', function(e) {
    if (e.target === createGroupModal) {
      createGroupModal.style.display = 'none';
    }
  });
  createGroupForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const groupName = document.getElementById('group-name').value;
    const investmentGoal = document.getElementById('investment-goal').value;
    const monthlyContribution = parseFloat(document.getElementById('monthly-contribution').value);
    const investmentType = document.getElementById('investment-type').value;
    // For demo, just add current user as member
    await apiPost('/group-investment', {
      name: groupName,
      goal: investmentGoal,
      monthlyContribution,
      investmentType,
      members: [{ id: userId }]
    });
    await renderInvestmentGroups();
    createGroupForm.reset();
    document.getElementById('selected-members').innerHTML = '';
  });
}
if (investmentGroupsList) {
  investmentGroupsList.addEventListener('click', async function(e) {
    const card = e.target.closest('.investment-group-card');
    const idx = Array.from(investmentGroupsList.children).indexOf(card);
    const groupId = card && card.dataset.id;
    if (e.target.classList.contains('add-contribution')) {
      const amount = parseFloat(prompt('Enter contribution amount:'));
      if (!isNaN(amount) && amount > 0) {
        await apiPost(`/group-investment/${groupId}/contribute?userId=${userId}&amount=${amount}`);
        await renderInvestmentGroups();
        await loadDashboard(userId);
      }
    }
  });
}
renderInvestmentGroups();

// --- Subscription Trimmer+ ---
const scanSubscriptionsBtn = document.getElementById('scan-subscriptions-btn');
const subscriptionsList = document.getElementById('subscriptions-list');
const subscriptionRecommendationsList = document.getElementById('subscription-recommendations-list');
const createPoolBtn = document.getElementById('create-pool-btn');
const subscriptionPoolsList = document.getElementById('subscription-pools-list');
async function renderSubscriptions() {
  console.log('Rendering Subscription Trimmer data');
  try {
    // Active subscriptions
    if (subscriptionsList) {
      subscriptionsList.innerHTML = '';
      const subs = await apiGet(`/subscriptions/user/${userId}`);
      
      if (subs && subs.length > 0) {
        // Calculate potential savings
        let totalMonthly = 0;
        let inactiveCount = 0;
        let sharedCount = 0;
        
        subs.forEach(sub => {
          const amount = parseFloat(sub.amount || 0);
          totalMonthly += amount;
          if (sub.status === 'Inactive' || !sub.isActive) inactiveCount++;
          if (sub.isShared) sharedCount++;
          
          // Determine subscription icon based on category or name
          let subIcon = '💳';
          let bgColor = 'bg-blue-50';
          
          const subName = (sub.name || '').toLowerCase();
          if (subName.includes('netflix') || subName.includes('prime') || subName.includes('hulu') || 
              subName.includes('disney') || sub.category === 'Entertainment') {
            subIcon = '🍿';
            bgColor = 'bg-red-50';
          }
          if (subName.includes('spotify') || subName.includes('apple music') || 
              subName.includes('youtube') || subName.includes('audio')) {
            subIcon = '🎵';
            bgColor = 'bg-green-50';
          }
          if (subName.includes('gym') || subName.includes('fitness') || 
              sub.category === 'Health') {
            subIcon = '🏋️';
            bgColor = 'bg-purple-50';
          }
          if (subName.includes('cloud') || subName.includes('drive') || 
              subName.includes('office') || sub.category === 'Productivity') {
            subIcon = '☁️';
            bgColor = 'bg-blue-50';
          }
          
          // Create subscription card
          const div = document.createElement('div');
          div.className = `subscription-item ${bgColor} p-4 rounded-lg shadow-sm mb-4 flex justify-between items-center`;
          div.dataset.id = sub.id;
          div.dataset.amount = amount;
          
          const nextBillingDate = sub.nextBillingDate ? new Date(sub.nextBillingDate) : new Date();
          const daysLeft = Math.ceil((nextBillingDate - new Date()) / (1000 * 60 * 60 * 24));
          
          div.innerHTML = `
            <div class="flex items-start gap-3">
              <div class="text-2xl mt-1">${subIcon}</div>
              <div>
                <h4 class="font-semibold text-gray-800">${sub.name}</h4>
                <p class="text-sm text-gray-600">Billing: ₹${amount.toLocaleString()} (${sub.billingCycle || 'Monthly'})</p>
                <p class="text-sm text-gray-600">Next billing: <span class="${daysLeft < 7 ? 'text-red-600 font-medium' : ''}">${nextBillingDate.toLocaleDateString()} (${daysLeft} days)</span></p>
                ${sub.lastUsed ? `<p class="text-xs text-gray-500">Last used: ${new Date(sub.lastUsed).toLocaleDateString()}</p>` : ''}
                ${sub.isShared ? `<p class="text-xs text-green-600">Shared with: ${sub.sharedWith || 'others'}</p>` : ''}
              </div>
            </div>
            <div class="flex flex-col items-end gap-2">
              <span class="${sub.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'} px-2 py-1 rounded text-xs font-bold">
                ${sub.status || (sub.isActive ? 'Active' : 'Inactive')}
              </span>
              <div class="flex gap-1">
                <button class="btn secondary toggle-status px-2 py-1 rounded text-xs ${sub.isActive ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}">
                  ${sub.isActive ? 'Pause' : 'Activate'}
                </button>
                ${!sub.isShared ? `<button class="btn secondary share-subscription px-2 py-1 rounded text-xs bg-blue-100 text-blue-700">Share</button>` : ''}
              </div>
            </div>
          `;
          subscriptionsList.appendChild(div);
        });
        
        // Add summary card
        const summaryDiv = document.createElement('div');
        summaryDiv.className = 'subscription-summary bg-white p-4 rounded-lg shadow-md mb-4 mt-6';
        
        // Calculate potential savings
        const inactiveSavings = inactiveCount * (totalMonthly / subs.length) * 0.8; // Estimate
        const sharingSavings = (subs.length - sharedCount - inactiveCount) * (totalMonthly / subs.length) * 0.5; // Estimate
        const totalPotentialSavings = inactiveSavings + sharingSavings;
        
        summaryDiv.innerHTML = `
          <h4 class="font-semibold text-lg mb-2">Subscription Summary</h4>
          <div class="grid grid-cols-2 gap-4 mb-3">
            <div>
              <p class="text-sm text-gray-600">Total Monthly Cost:</p>
              <p class="text-xl font-bold text-gray-800">₹${totalMonthly.toLocaleString()}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Annual Cost:</p>
              <p class="text-xl font-bold text-gray-800">₹${(totalMonthly * 12).toLocaleString()}</p>
            </div>
          </div>
          
          <div class="bg-green-50 p-3 rounded-lg mb-3">
            <h5 class="font-medium text-green-800 mb-1">Potential Savings</h5>
            <div class="grid grid-cols-2 gap-2 text-sm">
              <p>By canceling unused: <span class="font-medium text-green-700">₹${inactiveSavings.toLocaleString(undefined, {maximumFractionDigits: 0})}/month</span></p>
              <p>By sharing more: <span class="font-medium text-green-700">₹${sharingSavings.toLocaleString(undefined, {maximumFractionDigits: 0})}/month</span></p>
            </div>
            <p class="text-right text-green-700 font-bold mt-1">Total: ₹${totalPotentialSavings.toLocaleString(undefined, {maximumFractionDigits: 0})}/month</p>
          </div>
          
          <div class="text-xs text-gray-500">
            <p>Optimize your subscriptions by pausing unused services or sharing with friends and family.</p>
          </div>
        `;
        subscriptionsList.appendChild(summaryDiv);
        
      } else {
        // Show placeholder or empty state
        subscriptionsList.innerHTML = `
          <div class="empty-state bg-gray-50 p-6 rounded-lg text-center">
            <p class="text-gray-500 mb-3">No subscriptions found</p>
            <p class="text-sm text-gray-400">Click "Scan for Subscriptions" to detect your recurring payments!</p>
          </div>
        `;
      }
    }
    
    // Subscription pool recommendations
    if (subscriptionRecommendationsList) {
      subscriptionRecommendationsList.innerHTML = '';
      
      try {
        // Try to fetch recommendations if endpoint exists
        const recommendations = await apiGet(`/subscriptions/user/${userId}/recommendations`);
        
        if (recommendations && recommendations.length > 0) {
          renderRecommendations(recommendations);
        } else {
          createSampleRecommendations();
        }
      } catch (err) {
        // If endpoint not implemented, show sample data
        console.log('Subscription recommendations endpoint not available, showing sample data');
        createSampleRecommendations();
      }
    }
    
    function createSampleRecommendations() {
      // Create sample recommendations if real data is not available
      const sampleRecommendations = [
        { name: 'Netflix', currentCost: 799, sharedCost: 249, potentialSavings: 550, sharingOption: 'Family Plan (4 users)', eligibleFriends: ['Aarav', 'Diya'] },
        { name: 'Spotify', currentCost: 119, sharedCost: 39, potentialSavings: 80, sharingOption: 'Family Plan (6 users)', eligibleFriends: ['Aarav', 'Diya', 'Rishi'] },
        { name: 'Amazon Prime', currentCost: 1499/12, sharedCost: 499/12, potentialSavings: 1000/12, sharingOption: 'Family Account (6 users)', eligibleFriends: ['Rishi', 'Ananya'] }
      ];
      
      renderRecommendations(sampleRecommendations);
    }
    
    function renderRecommendations(recommendations) {
      recommendations.forEach(rec => {
        const div = document.createElement('div');
        div.className = 'recommendation-item bg-white p-3 rounded-lg shadow-sm mb-3 border-l-4 border-green-400';
        
        const monthlySavings = rec.potentialSavings;
        const annualSavings = monthlySavings * 12;
        const eligibleFriends = rec.eligibleFriends || [];
        
        div.innerHTML = `
          <div class="flex justify-between items-start">
            <div>
              <h4 class="font-medium">${rec.name}</h4>
              <p class="text-xs text-gray-500">Sharing option: ${rec.sharingOption}</p>
            </div>
            <div class="text-right">
              <p class="text-sm">Current: <span class="font-medium text-red-600">₹${rec.currentCost.toLocaleString()}</span>/mo</p>
              <p class="text-sm">Shared: <span class="font-medium text-green-600">₹${rec.sharedCost.toLocaleString()}</span>/mo</p>
            </div>
          </div>
          <div class="mt-2 flex justify-between items-center">
            <div>
              <p class="text-xs text-gray-600">Compatible with: 
                ${eligibleFriends.map(friend => `<span class="inline-block bg-blue-100 text-blue-700 rounded px-1 mx-0.5 text-xs">${friend}</span>`).join('')}
              </p>
            </div>
            <div class="text-right">
              <p class="text-sm font-bold text-green-600">Save ₹${monthlySavings.toLocaleString()}/mo</p>
              <p class="text-xs text-green-600">₹${annualSavings.toLocaleString()}/year</p>
            </div>
          </div>
          <div class="mt-2">
            <button class="w-full bg-green-100 text-green-700 px-2 py-1 rounded text-sm font-medium">Create Sharing Group</button>
          </div>
        `;
        
        subscriptionRecommendationsList.appendChild(div);
      });
    }
    
    console.log('Subscription Trimmer data rendered successfully');
  } catch (err) {
    console.error('Error rendering Subscription Trimmer data:', err);
  }
}
if (scanSubscriptionsBtn) {
  scanSubscriptionsBtn.addEventListener('click', renderSubscriptions);
}

// Navigation code is now at the top of the file
if (subscriptionsList) {
  subscriptionsList.addEventListener('click', async function(e) {
    if (e.target.classList.contains('toggle-status')) {
      const idx = Array.from(subscriptionsList.children).indexOf(e.target.closest('.subscription-item'));
      // Optionally, implement status toggle via backend
      alert('Status toggle backend integration needed');
    }
  });
}
if (createPoolBtn) {
  createPoolBtn.addEventListener('click', function() {
    alert('Subscription pool creation backend integration needed');
  });
}
renderSubscriptions();

// --- Financial Reports (Backend-Powered) ---
const monthlyReportBtn = document.getElementById('monthly-report-btn');
const quarterlyReportBtn = document.getElementById('quarterly-report-btn');
const yearlyReportBtn = document.getElementById('yearly-report-btn');
const reportPreviewContainer = document.getElementById('report-preview-container');
const reportActions = document.querySelector('.report-actions');

async function fetchAndRenderReport(type) {
    if (!reportPreviewContainer) return;
    reportPreviewContainer.innerHTML = '<p>Generating your financial report...</p>';
    try {
        // Call backend analytics/report endpoint
        const report = await apiGet(`/reports/user/${userId}?type=${type}`);
        let reportHtml = '';
        if (type === 'monthly') {
            reportHtml = `
                <div class="report-story">
                    <div class="report-header">
                        <h3>Your ${report.period} Financial Wrap</h3>
                    </div>
                    <div class="report-stats">
                        <div class="report-stat">
                            <h4>Total Saved</h4>
                            <p class="big-number">₹${report.totalSaved}</p>
                        </div>
                        <div class="report-stat">
                            <h4>Saving Streak</h4>
                            <p class="big-number">${report.savingStreak} months</p>
                        </div>
                        <div class="report-stat">
                            <h4>Goal Progress</h4>
                            <p class="big-number">${report.goalProgress}%</p>
                        </div>
                    </div>
                    <div class="report-highlight">
                        <h4>Biggest Win</h4>
                        <p>${report.biggestWin}</p>
                    </div>
                    <div class="report-badge">
                        <h4>Badge Earned</h4>
                        <p>${report.badge}</p>
                    </div>
                </div>
            `;
        } else if (type === 'quarterly') {
            reportHtml = `
                <div class="report-story">
                    <div class="report-header">
                        <h3>${report.period} Financial Review</h3>
                    </div>
                    <div class="report-stats">
                        <div class="report-stat">
                            <h4>Total Saved</h4>
                            <p class="big-number">₹${report.totalSaved}</p>
                        </div>
                        <div class="report-stat">
                            <h4>Investment Growth</h4>
                            <p class="big-number">${report.investmentGrowth}%</p>
                        </div>
                        <div class="report-stat">
                            <h4>Expenses Reduced</h4>
                            <p class="big-number">₹${report.expensesReduced}</p>
                        </div>
                    </div>
                    <div class="report-highlight">
                        <h4>Top Achievement</h4>
                        <p>${report.topAchievement}</p>
                    </div>
                    <div class="report-badge">
                        <h4>Badge Earned</h4>
                        <p>${report.badge}</p>
                    </div>
                </div>
            `;
        } else if (type === 'yearly') {
            reportHtml = `
                <div class="report-story">
                    <div class="report-header">
                        <h3>Your ${report.period} Financial Journey</h3>
                    </div>
                    <div class="report-stats">
                        <div class="report-stat">
                            <h4>Total Saved</h4>
                            <p class="big-number">₹${report.totalSaved}</p>
                        </div>
                        <div class="report-stat">
                            <h4>Net Worth Growth</h4>
                            <p class="big-number">${report.netWorthGrowth}%</p>
                        </div>
                        <div class="report-stat">
                            <h4>Goals Achieved</h4>
                            <p class="big-number">${report.goalsAchieved}</p>
                        </div>
                    </div>
                    <div class="report-highlight">
                        <h4>Year Highlight</h4>
                        <p>${report.yearHighlight}</p>
                    </div>
                    <div class="report-badge">
                        <h4>Badge Earned</h4>
                        <p>${report.badge}</p>
                    </div>
                </div>
            `;
        }
        reportPreviewContainer.innerHTML = reportHtml;
        if (reportActions) reportActions.style.display = 'flex';
    } catch (err) {
        reportPreviewContainer.innerHTML = `<p class="text-red-600">Failed to load report: ${err.message}</p>`;
    }
}

if (monthlyReportBtn) monthlyReportBtn.addEventListener('click', () => fetchAndRenderReport('monthly'));
if (quarterlyReportBtn) quarterlyReportBtn.addEventListener('click', () => fetchAndRenderReport('quarterly'));
if (yearlyReportBtn) yearlyReportBtn.addEventListener('click', () => fetchAndRenderReport('yearly'));



// --- Dynamic Donut Chart for Spending Insights (reads DOM data) ---
function renderDonutChartFromDOM() {
    const valueSpans = document.querySelectorAll('.category-value');
    const categories = [];
    valueSpans.forEach(span => {
        const label = span.dataset.category;
        const colorMap = {
            'Food': '#22c55e',
            'Shopping': '#facc15',
            'Entertainment': '#3b82f6',
            'Transport': '#ef4444',
            'Others': '#6b7280'
        };
        const value = parseInt(span.textContent.replace(/[^\d]/g, ''));
        categories.push({ label, value, color: colorMap[label] });
    });
    if (categories.length === 0) return;
    const total = categories.reduce((sum, c) => sum + c.value, 0);
    const radius = 70;
    const circumference = 2 * Math.PI * radius;
    let offset = 0;
    let svg = `<svg width="180" height="180" viewBox="0 0 180 180">
        <circle r="70" cx="90" cy="90" fill="transparent" stroke="#e5e7eb" stroke-width="24"/>
    `;
    categories.forEach(cat => {
        const percent = cat.value / total;
        const dash = percent * circumference;
        svg += `<circle r="70" cx="90" cy="90" fill="transparent" stroke="${cat.color}" stroke-width="24" stroke-dasharray="${dash} ${circumference}" stroke-dashoffset="-${offset}"/>`;
        offset += dash;
    });
    svg += `<text x="50%" y="50%" text-anchor="middle" dy=".3em" font-size="1.5rem" fill="#22c55e" font-weight="bold">Spending</text></svg>`;
    document.getElementById('donut-chart').innerHTML = svg;
}
renderDonutChartFromDOM();
Array.from(document.querySelectorAll('.spending-insights .tab')).forEach(tab => {
    tab.addEventListener('click', () => setTimeout(renderDonutChartFromDOM, 100));
});

// --- Demo: Dynamic Spending Insights Tab Switching ---
// (Optional: Replace with backend-powered analytics if available)

}); // Close the DOMContentLoaded event handler