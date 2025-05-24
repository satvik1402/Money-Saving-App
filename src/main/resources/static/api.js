const API_BASE = "http://localhost:8080/api";

export async function apiGet(path) {
    console.log(`Fetching data from: ${API_BASE}${path}`);
    try {
        const res = await fetch(`${API_BASE}${path}`);
        if (!res.ok) {
            const errorText = await res.text();
            console.error(`Error fetching ${path}:`, errorText);
            throw new Error(errorText);
        }
        const data = await res.json();
        console.log(`Data received from ${path}:`, data);
        return data;
    } catch (error) {
        console.error(`Failed request to ${path}:`, error);
        // Return empty data rather than crashing the app
        return path.includes('/user/') ? {} : [];
    }
}

export async function apiPost(path, data) {
    console.log(`Posting data to: ${API_BASE}${path}`);
    try {
        const res = await fetch(`${API_BASE}${path}` , {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            const errorText = await res.text();
            console.error(`Error posting to ${path}:`, errorText);
            throw new Error(errorText);
        }
        const responseData = await res.json();
        console.log(`Data received from ${path}:`, responseData);
        return responseData;
    } catch (error) {
        console.error(`Failed request to ${path}:`, error);
        // Return empty data rather than crashing the app
        return {};
    }
}

export async function apiPut(path, data) {
    console.log(`Putting data to: ${API_BASE}${path}`);
    try {
        const res = await fetch(`${API_BASE}${path}` , {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });
        if (!res.ok) {
            const errorText = await res.text();
            console.error(`Error putting to ${path}:`, errorText);
            throw new Error(errorText);
        }
        const responseData = await res.json();
        console.log(`Data received from ${path}:`, responseData);
        return responseData;
    } catch (error) {
        console.error(`Failed request to ${path}:`, error);
        return {};
    }
}

export async function apiDelete(path) {
    console.log(`Deleting at: ${API_BASE}${path}`);
    try {
        const res = await fetch(`${API_BASE}${path}`, { method: "DELETE" });
        if (!res.ok) {
            const errorText = await res.text();
            console.error(`Error deleting at ${path}:`, errorText);
            throw new Error(errorText);
        }
        // Some DELETE endpoints may not return JSON
        try {
            const responseData = await res.json();
            return responseData;
        } catch (e) {
            return { success: true };
        }
    } catch (error) {
        console.error(`Failed delete request to ${path}:`, error);
        return { success: false, error: error.message };
    }
}